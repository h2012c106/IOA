package com.IOA.service;

import com.IOA.dao.*;
import com.IOA.model.*;
import com.IOA.util.MyErrorType;
import com.IOA.util.MyPipe;
import com.IOA.util.TokenManager;
import com.IOA.vo.NormalMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClusterService {

    @Autowired
    GreenhouseClusterDAO GCDAO;

    @Autowired
    ClusterDAO CDAO;

    @Autowired
    ClusterDeviceDAO CDDAO;

    @Autowired
    ClusterSensorDAO CSDAO;

    @Autowired
    DeviceDAO DDAO;

    @Autowired
    SensorDAO SDAO;

    @Autowired
    SelectDAO SeDAO;

    @Autowired
    SensorThresholdDAO STDAO;

    @Autowired
    ThresholdDAO TDAO;

    @Autowired
    ResultDAO RDAO;

    @Autowired
    MyPipe Pipe;


    public NormalMessage unbind(String token, String clusterId, Integer greenhouseId) {

        // 找到所有大棚内所有传感器群的所有设备
        List<ClusterDeviceModel> abandonedDeviceArr
                = CDDAO.searchBySomeId(clusterId, "clusterId");
        List<Object> abandonedDeviceIdArr = abandonedDeviceArr.stream()
                .map(ClusterDeviceModel::getDeviceId)
                .collect(Collectors.toList());
        // 找到所有大棚内所有传感器群的所有传感器
        List<ClusterSensorModel> abandonedSensorArr
                = CSDAO.searchBySomeId(clusterId, "clusterId");
        List<Object> abandonedSensorIdArr = abandonedSensorArr.stream()
                .map(ClusterSensorModel::getSensorId)
                .collect(Collectors.toList());
        // 找到所有大棚内所有传感器群的所有传感器的所有阈值
        List<SensorThresholdModel> abandonedThresholdArr
                = STDAO.searchBySomeId(abandonedSensorIdArr, "sensorId");
        List<Object> abandonedThresholdIdArr = abandonedThresholdArr.stream()
                .map(SensorThresholdModel::getThresholdId)
                .collect(Collectors.toList());

        // 删除传感器对应的阈值对以及其目前选择的阈值
        SeDAO.deleteBySomeId(abandonedSensorIdArr, "sensorId");
        STDAO.deleteBySomeId(abandonedSensorIdArr, "sensorId");
        TDAO.deleteBySomeId(abandonedThresholdIdArr, "id");

        // 将所有设备全部关闭
        DDAO.closeAll(abandonedDeviceIdArr);

        // 将缓存内所有结果清除
        Pipe.clearCluster(clusterId);

        // 将所有传感器群解绑并且把没问题的关掉
        GCDAO.deleteBySomeId(clusterId, "greenhouseId");
        CDAO.updateStatus(clusterId, "close");

        return new NormalMessage(true, null, null);
    }

    public NormalMessage getInfo(String token, String clusterId) {
        List<GreenhouseClusterModel> justForName
                = GCDAO.searchBySomeId(clusterId, "clusterId");
        List<ClusterModel> singleCluster
                = CDAO.searchBySomeId(clusterId, "clusterId");
        if (justForName.size() == 0 || singleCluster.size() == 0) {
            return new NormalMessage(false, MyErrorType.ClusterUnexist, null);
        } else {
            Map<String, String> message = new HashMap<>();
            message.put("clusterId", clusterId);
            message.put("name", justForName.get(0).getName());
            message.put("status", singleCluster.get(0).getStatus());
            return new NormalMessage(true, null, message);
        }
    }

    public NormalMessage alterInfo(String token, String clusterId,
                                   String newName, String newStatus) {

        // 拿到这个传感器群所属的大棚，然后搜索大棚内是否会出现重名情况
        List<GreenhouseClusterModel> singleCluster
                = GCDAO.searchBySomeId(clusterId, "clusterId");
        if (singleCluster.size() == 0) {
            return new NormalMessage(false, MyErrorType.ClusterUnexist, null);
        }
        int greenhouseId = singleCluster.get(0).getGreenhouseId();
        List<GreenhouseClusterModel> sameNameClusterArr
                = GCDAO.searchBySomeId(greenhouseId, "greenhouseId", newName, "name");
        // 当大棚下有同名传感器群且此群与现在要改的群不是同一个群
        if (sameNameClusterArr.size() != 0
                && !sameNameClusterArr.get(0).getClusterId().equals(clusterId)) {
            return new NormalMessage(false, MyErrorType.ClusterNameDuplicate, null);
        }

        GCDAO.updateName(clusterId, newName);
        return CDAO.updateStatus(clusterId, newStatus)
                ? new NormalMessage(true, null, null)
                : new NormalMessage(false, MyErrorType.UpdateError, null);
    }

    public NormalMessage getStatus(String token, String clusterId) {
        // 试着从缓存里拿更新时间
        Timestamp resultTime = Pipe.getRefreshTime(clusterId);

        List<Map<String, Object>> sensorArr = new ArrayList<>();

        // 拿到这个传感器群对应的传感器信息
        List<ClusterSensorModel> sensorOfClusterArr
                = CSDAO.searchBySomeId(clusterId, "clusterId");
        List<Object> sensorIdOfClusterArr = sensorOfClusterArr.stream()
                .map(ClusterSensorModel::getSensorId)
                .collect(Collectors.toList());

        // 试着从缓存中拿出这个传感器的信息以及时间
        Map<Integer, Map<String, BigDecimal>> SensorValuesMap = Pipe.getSensor2Server(clusterId);
        // 若缓存中没有这个传感器群的信息，那么去数据库中找
        if (SensorValuesMap == null) {
            SensorValuesMap = new HashMap<>();
            // 先拿到这个传感器属于的大棚，避免看数据时看到这个传感器在先前的大棚里的数据
            List<GreenhouseClusterModel> sensorGreenhouseModelArr
                    = GCDAO.searchBySomeId(clusterId, "clusterId");
            if (sensorGreenhouseModelArr.size() == 0) {
                return new NormalMessage(false, MyErrorType.ClusterUnexist, null);
            }

            // 填Map
            Integer greenhouseId = sensorGreenhouseModelArr.get(0).getGreenhouseId();
            for (Object tmpSensorIdObj : sensorIdOfClusterArr) {
                Integer sensorId = (Integer) tmpSensorIdObj;
                // 拿到该传感器在此大棚内的最新数据
                ResultModel result = RDAO.searchLatestResult(sensorId, greenhouseId);
                if (result != null) {
                    Map<String, BigDecimal> tmpMap = new HashMap<>();
                    tmpMap.put("value", result.getValue());
                    tmpMap.put("minimum", result.getMinimum());
                    tmpMap.put("maximum", result.getMaximum());

                    SensorValuesMap.put(sensorId, tmpMap);
                    Timestamp tmpTime = result.getTime();
                    resultTime = resultTime.before(tmpTime) ? tmpTime : resultTime;
                }
            }
        }

        List<SensorModel> sensorModelArr
                = SDAO.searchBySomeId(sensorIdOfClusterArr, "id");
        for (SensorModel tmpSensor : sensorModelArr) {
            Map<String, Object> tmpMap = new HashMap<>();

            int id = tmpSensor.getId();
            String type = tmpSensor.getType();
            String unit = tmpSensor.getUnit();

            BigDecimal value = SensorValuesMap.get(id).get("value");
            BigDecimal minimum = SensorValuesMap.get(id).get("minimum");
            BigDecimal maximum = SensorValuesMap.get(id).get("maximum");

            tmpMap.put("id", id);
            tmpMap.put("type", type);
            tmpMap.put("unit", unit);
            tmpMap.put("minimum", minimum);
            tmpMap.put("maximum", maximum);
            tmpMap.put("value", value);
            sensorArr.add(tmpMap);
        }

        List<Map<String, Object>> deviceArr = new ArrayList<>();

        // 拿到这个传感器群对应的设备信息
        List<ClusterDeviceModel> deviceOfClusterArr
                = CDDAO.searchBySomeId(clusterId, "clusterId");
        List<Object> deviceIdOfClusterArr = deviceOfClusterArr.stream()
                .map(ClusterDeviceModel::getDeviceId)
                .collect(Collectors.toList());
        List<DeviceModel> deviceModelArr
                = DDAO.searchBySomeId(deviceIdOfClusterArr, "id");

//        // 试着从缓存中拿出这个传感器的信息
//        Map<Integer, String> DeviceStatusMap = Pipe.getDevice2Server(clusterId);
//        // 若缓存中没有这个传感器群的信息，那么去数据库中找
//        if (DeviceStatusMap == null) {
//            DeviceStatusMap = new HashMap<>();
//            List<DeviceModel> deviceModelArr
//                    = DDAO.searchBySomeId(deviceIdOfClusterArr, "id");
//            for (DeviceModel tmpDevice : deviceModelArr) {
//                DeviceStatusMap.put(tmpDevice.getId(), tmpDevice.getStatus());
//            }
//        }

        for (DeviceModel tmpDevice : deviceModelArr) {
            Map<String, Object> tmpMap = new HashMap<>();

            Integer id = tmpDevice.getId();
            String name = tmpDevice.getName();
            String status = tmpDevice.getStatus();

            tmpMap.put("id", id);
            tmpMap.put("name", name);
            tmpMap.put("status", status);
            deviceArr.add(tmpMap);
        }

        Map<String, Object> message = new HashMap<>();
        message.put("sensorArr", sensorArr);
        message.put("deviceArr", deviceArr);
        message.put("time", resultTime);
        return new NormalMessage(true, null, message);
    }


}
