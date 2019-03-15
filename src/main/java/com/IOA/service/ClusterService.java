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
    SensorDAO SDAO;

    @Autowired
    DeviceDAO DDAO;

    @Autowired
    ResultDAO RDAO;

    @Autowired
    ThresholdDAO TDAO;

    @Autowired
    UserGreenhouseDAO UGDAO;

    @Autowired
    SensorGreenhouseDAO SGDAO;

    @Autowired
    MyPipe Pipe;


    public NormalMessage unbind(String token, String clusterId, Integer greenhouseId) {
//        Map<String, Object> userInfo = TokenManager.parseToken(token);
//        Integer id = (Integer) userInfo.get("id");
//
//        if (!UGDAO.doesGreenhouseBelongToUser(id, greenhouseId)) {
//            return new NormalMessage(false, MyErrorType.GreenhouseAuthorization, null);
//        }
//
//        if(SGDAO.searchBySomeId(clusterId,"clusterId"
//                ,greenhouseId,"greenhouseId")
//                .size()==0){
//            return new NormalMessage(false, MyErrorType.ClusterAuthorization, null);
//        }

        // 把传感器群内传感器的阈值设定全部消除
        SDAO.updateThreshold(clusterId);
        List<Integer> sensorIdArr = SDAO.searchBySomeId(clusterId, "clusterId")
                .stream()
                .map(SensorModel::getId)
                .collect(Collectors.toList());
        TDAO.deleteBySomeId(sensorIdArr, "sensorId");

        // 清空缓存中该传感器的数据
        Pipe.clearCluster(clusterId);

        // 把传感器群内的设备全部关闭
        DDAO.closeAll(clusterId);

        SGDAO.deleteBySomeId(clusterId, "clusterId", greenhouseId, "greenhouseId");

        return new NormalMessage(true, null, null);
    }

    public NormalMessage getInfo(String token, String clusterId) {
        List<SensorGreenhouseModel> clusterArr
                = SGDAO.searchBySomeId(clusterId, "clusterId");
        return clusterArr.size() == 0
                ? new NormalMessage(false, MyErrorType.ClusterUnexist, null)
                : new NormalMessage(true, null, clusterArr.get(0));
    }

    public NormalMessage alterInfo(String token, SensorGreenhouseModel cluster) {
        String newName = cluster.getName();
        String newState = cluster.getStatus();
        String clusterId = cluster.getClusterId();

        // 拿到这个传感器群所属的大棚，然后搜索大棚内是否会出现重名情况
        List<SensorGreenhouseModel> singleCLuster
                = SGDAO.searchBySomeId(clusterId, "clusterId");
        if (singleCLuster.size() == 0) {
            return new NormalMessage(false, MyErrorType.ClusterUnexist, null);
        }
        int greenhouseId = singleCLuster.get(0).getGreenhouseId();
        if (SGDAO.searchBySomeId(greenhouseId, "greenhouseId", newName, "name").size() != 0) {
            return new NormalMessage(false, MyErrorType.ClusterNameDuplicate, null);
        }

        SGDAO.updateClusterInfo(clusterId, newState, newName);
        return new NormalMessage(true, null, null);
    }

    public NormalMessage getStatus(String token, String clusterId) {
        // 试着从缓存里拿更新时间
        Timestamp resultTime = Pipe.getRefreshTime(clusterId);

        List<Map<String, Object>> sensorArr = new ArrayList<>();

        // 拿到这个传感器群对应的传感器信息
        List<SensorModel> sensorModelArr = SDAO.searchBySomeId(clusterId, "clusterId");

        // 试着从缓存中拿出这个传感器的信息以及时间
        Map<Integer, Map<String, BigDecimal>> SensorValuesMap = Pipe.getSensor2Server(clusterId);
        // 若缓存中没有这个传感器群的信息，那么去数据库中找
        if (SensorValuesMap == null) {
            SensorValuesMap = new HashMap<>();
            // 先拿到这个传感器属于的大棚，避免看数据时看到这个传感器在先前的大棚里的数据
            List<SensorGreenhouseModel> sensorGreenhouseModelArr
                    = SGDAO.searchBySomeId(clusterId, "clusterId");
            if (sensorGreenhouseModelArr.size() == 0) {
                return new NormalMessage(false, MyErrorType.ClusterUnexist, null);
            }

            // 填Map
            Integer greenhouseId = sensorGreenhouseModelArr.get(0).getGreenhouseId();
            for (SensorModel tmpSensor : sensorModelArr) {
                Integer sensorId = tmpSensor.getId();
                // 拿到该传感器在此大棚内的最新数据
                List<ResultModel> resultArr = RDAO.searchLatestResult(sensorId, greenhouseId);
                if (resultArr.size() != 0) {
                    Map<String, BigDecimal> tmpMap = new HashMap<>();
                    tmpMap.put("value", resultArr.get(0).getValue());
                    tmpMap.put("minimum", resultArr.get(0).getMinimum());
                    tmpMap.put("maximum", resultArr.get(0).getMaximum());

                    SensorValuesMap.put(sensorId, tmpMap);
                    Timestamp tmpTime = resultArr.get(0).getTime();
                    resultTime = resultTime.before(tmpTime) ? tmpTime : resultTime;
                }
            }
        }

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
        List<DeviceModel> deviceModelArr = DDAO.searchBySomeId(clusterId, "clusterId");

        // 试着从缓存中拿出这个传感器的信息
        Map<Integer, String> DeviceStatusMap = Pipe.getDevice2Server(clusterId);
        // 若缓存中没有这个传感器群的信息，那么去数据库中找
        if (DeviceStatusMap == null) {
            DeviceStatusMap = new HashMap<>();
            for (DeviceModel tmpDevice : deviceModelArr) {
                DeviceStatusMap.put(tmpDevice.getId(), tmpDevice.getStatus());
            }
        }

        for (DeviceModel tmpDevice : deviceModelArr) {
            Map<String, Object> tmpMap = new HashMap<>();

            int id = tmpDevice.getId();
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
