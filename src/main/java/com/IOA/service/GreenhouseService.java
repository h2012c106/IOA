package com.IOA.service;

import com.IOA.dao.*;
import com.IOA.model.*;
import com.IOA.util.MyErrorType;
import com.IOA.util.MyPipe;
import com.IOA.util.TokenManager;
import com.IOA.dto.NormalMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GreenhouseService {

    @Autowired
    UserDAO UDAO;

    @Autowired
    UserGreenhouseDAO UGDAO;

    @Autowired
    GreenhouseDAO GDAO;

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
    SelectDAO SeDAO;

    @Autowired
    SensorThresholdDAO STDAO;

    @Autowired
    ThresholdDAO TDAO;

    @Autowired
    MyPipe Pipe;

    public NormalMessage getInfo(String token, Integer greenhouseId) {
        Map<String, Object> userInfo = TokenManager.parseToken(token);
        Integer id = (Integer) userInfo.get("id");

        if (!UGDAO.doesGreenhouseBelongToUser(id, greenhouseId)) {
            return new NormalMessage(false, MyErrorType.GreenhouseAuthorization, null);
        }

        List<GreenhouseModel> greenhouseArr = GDAO
                .searchBySomeId(greenhouseId, "id");
        if (greenhouseArr.size() == 0) {
            return new NormalMessage(false, MyErrorType.GreenhouseAuthorization, null);
        } else {
            return new NormalMessage(true, null, greenhouseArr.get(0));
        }
    }

    public NormalMessage alterInfo(String token, GreenhouseModel greenhouse) {
        Map<String, Object> userInfo = TokenManager.parseToken(token);
        Integer id = (Integer) userInfo.get("id");

        if (!UGDAO.doesGreenhouseBelongToUser(id, greenhouse.getId())) {
            return new NormalMessage(false, MyErrorType.GreenhouseAuthorization, null);
        }

        // 大棚不可重名
        List<UserGreenhouseModel> userAllGreenhouse = UGDAO.searchBySomeId(id, "userId");
        List<Object> userAllGreenhouseId = userAllGreenhouse.stream()
                .map(UserGreenhouseModel::getGreenhouseId)
                .collect(Collectors.toList());
        if (GDAO.isNameDuplicate(userAllGreenhouseId, greenhouse.getName(), greenhouse.getId())) {
            return new NormalMessage(false, MyErrorType.GreenhouseNameDuplicate, null);
        }

        // 大棚开启或关闭，会连同操作其内未损坏的传感器群状态，如果大棚损坏，那么将其内除去损坏的传感器群全部关闭
        List<GreenhouseClusterModel> clusterArrOfGreenhouse
                = GCDAO.searchBySomeId(greenhouse.getId(), "greenhouseId");
        List<Object> clusterIdArrOfGreenhouse
                = clusterArrOfGreenhouse.stream()
                .map(GreenhouseClusterModel::getClusterId)
                .collect(Collectors.toList());
        if (greenhouse.getStatus().equals("close") || greenhouse.getStatus().equals("on")) {
            CDAO.updateStatus(clusterIdArrOfGreenhouse, greenhouse.getStatus());
        } else {
            CDAO.updateStatus(clusterIdArrOfGreenhouse, "close");
        }

        return GDAO.update(greenhouse)
                ? new NormalMessage(true, null, null)
                : new NormalMessage(false, MyErrorType.UpdateError, null);
    }

    public NormalMessage unbind(String token, int greenhouseId) {
        Map<String, Object> userInfo = TokenManager.parseToken(token);
        Integer id = (Integer) userInfo.get("id");

        if (!UGDAO.doesGreenhouseBelongToUser(id, greenhouseId)) {
            return new NormalMessage(false, MyErrorType.GreenhouseAuthorization, null);
        }

        // 找到所有大棚内所有传感器群
        List<GreenhouseClusterModel> abandonedClusterArr
                = GCDAO.searchBySomeId(greenhouseId, "greenhouseId");
        List<Object> abandonedClusterIdArr = abandonedClusterArr.stream()
                .map(GreenhouseClusterModel::getClusterId)
                .collect(Collectors.toList());
        // 找到所有大棚内所有传感器群的所有设备
        List<ClusterDeviceModel> abandonedDeviceArr
                = CDDAO.searchBySomeId(abandonedClusterIdArr, "clusterId");
        List<Object> abandonedDeviceIdArr = abandonedDeviceArr.stream()
                .map(ClusterDeviceModel::getDeviceId)
                .collect(Collectors.toList());
        // 找到所有大棚内所有传感器群的所有传感器
        List<ClusterSensorModel> abandonedSensorArr
                = CSDAO.searchBySomeId(abandonedClusterIdArr, "clusterId");
        List<Object> abandonedSensorIdArr = abandonedSensorArr.stream()
                .map(ClusterSensorModel::getSensorId)
                .collect(Collectors.toList());
        // 找到所有大棚内所有传感器群的所有传感器的所有阈值
        List<SensorThresholdModel> abandonedThresholdArr
                = STDAO.searchBySomeId(abandonedSensorIdArr, "sensorId");
        List<Object> abandonedThresholdIdArr = abandonedThresholdArr.stream()
                .map(SensorThresholdModel::getThresholdId)
                .collect(Collectors.toList());
        System.out.println(abandonedClusterArr);
        System.out.println(abandonedDeviceIdArr);
        System.out.println(abandonedSensorArr);

        // 删除传感器对应的阈值对以及其目前选择的阈值
        SeDAO.deleteBySomeId(abandonedSensorIdArr, "sensorId");
        STDAO.deleteBySomeId(abandonedSensorIdArr, "sensorId");
        TDAO.deleteBySomeId(abandonedThresholdIdArr, "id");

        // 将所有设备全部关闭
        DDAO.closeAll(abandonedDeviceIdArr);

        // 将缓存内所有结果清除
        Pipe.clearCluster(abandonedClusterIdArr);

        // 将所有传感器群解绑并且把没问题的关掉
        GCDAO.deleteBySomeId(greenhouseId, "greenhouseId");
        CDAO.updateStatus(abandonedClusterIdArr, "close");

        // 将所有大棚解绑并删除
        UGDAO.deleteBySomeId(greenhouseId, "greenhouseId");
        GDAO.deleteBySomeId(greenhouseId, "id");

        return new NormalMessage(true, null, null);
    }

    public NormalMessage bindCluster(String token, String clusterId, Integer greenhouseId,
                                     String pwd, String name) {
        Map<String, Object> userInfo = TokenManager.parseToken(token);
        Integer id = (Integer) userInfo.get("id");

        if (!UGDAO.doesGreenhouseBelongToUser(id, greenhouseId)) {
            return new NormalMessage(false, MyErrorType.GreenhouseAuthorization, null);
        }

        if (CDAO.searchBySomeId(clusterId, "id", pwd, "pwd").size() == 0) {
            return new NormalMessage(false, MyErrorType.ClusterVerificationError, null);
        }

        if (GCDAO.searchBySomeId(clusterId, "clusterId").size() != 0) {
            return new NormalMessage(false, MyErrorType.ClusterBeingUsed, null);
        }

        if (GCDAO.searchBySomeId(greenhouseId, "greenhouseId", name, "name").size() != 0) {
            return new NormalMessage(false, MyErrorType.ClusterNameDuplicate, null);
        }

        GCDAO.save(new GreenhouseClusterModel(clusterId, greenhouseId, name));

        return new NormalMessage(true, null, null);
    }

    public NormalMessage getClusterList(String token, Integer greenhouseId) {
        Map<String, Object> userInfo = TokenManager.parseToken(token);
        Integer id = (Integer) userInfo.get("id");

        if (!UGDAO.doesGreenhouseBelongToUser(id, greenhouseId)) {
            return new NormalMessage(false, MyErrorType.GreenhouseAuthorization, null);
        }

        Map<String, List<Map<String, String>>> message = new HashMap<>();
        List<Map<String, String>> clusterArr = new ArrayList<>();
        List<GreenhouseClusterModel> clusterArrOfGreenhouse
                = GCDAO.searchBySomeId(greenhouseId, "greenhouseId");
        for (GreenhouseClusterModel gc : clusterArrOfGreenhouse) {
            String tmpClusterId = gc.getClusterId();
            List<ClusterModel> tmpCluster = CDAO.searchBySomeId(tmpClusterId, "id");
            if (tmpCluster.size() != 0) {
                Map<String, String> tmpMap = new HashMap<>();
                tmpMap.put("clusterId", tmpClusterId);
                tmpMap.put("name", gc.getName());
                tmpMap.put("pwd", tmpCluster.get(0).getPwd());
                tmpMap.put("status", tmpCluster.get(0).getStatus());
                clusterArr.add(tmpMap);
            }
        }
        message.put("clusterArr", clusterArr);

        return new NormalMessage(true, null, message);
    }


    public NormalMessage getSomeGreenhouseInfo(Integer greenhouseId) {
        List<GreenhouseModel> greenhouseArr
                = GDAO.searchBySomeId(greenhouseId, "id");
        if (greenhouseArr.size() == 0) {
            return new NormalMessage(false, MyErrorType.GreenhouseAuthorization, null);
        } else {
            return new NormalMessage(true, null, greenhouseArr.get(0));
        }
    }

    public NormalMessage getSomeGreenhouseClusterList(Integer greenhouseId){
        List<Map<String, String>> clusterArr = new ArrayList<>();
        List<GreenhouseClusterModel> clusterArrOfGreenhouse
                = GCDAO.searchBySomeId(greenhouseId, "greenhouseId");
        for (GreenhouseClusterModel gc : clusterArrOfGreenhouse) {
            String tmpClusterId = gc.getClusterId();
            List<ClusterModel> tmpCluster = CDAO.searchBySomeId(tmpClusterId, "id");
            if (tmpCluster.size() != 0) {
                Map<String, String> tmpMap = new HashMap<>();
                tmpMap.put("clusterId", tmpClusterId);
                tmpMap.put("name", gc.getName());
                tmpMap.put("pwd", tmpCluster.get(0).getPwd());
                tmpMap.put("status", tmpCluster.get(0).getStatus());
                clusterArr.add(tmpMap);
            }
        }
        Map<String, List<Map<String, String>>> message = new HashMap<>();
        message.put("clusterArr", clusterArr);

        return new NormalMessage(true, null, message);
    }
}
