package com.IOA.service;

import com.IOA.dao.*;
import com.IOA.model.*;
import com.IOA.util.MD5Manager;
import com.IOA.util.MyErrorType;
import com.IOA.util.MyPipe;
import com.IOA.util.TokenManager;
import com.IOA.vo.NormalMessage;
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
    GreenhouseDAO GDAO;

    @Autowired
    SensorDAO SDAO;

    @Autowired
    DeviceDAO DDAO;

    @Autowired
    ThresholdDAO TDAO;

    @Autowired
    ResultDAO RDAO;

    @Autowired
    UserGreenhouseDAO UGDAO;

    @Autowired
    SensorGreenhouseDAO SGDAO;

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
        List<GreenhouseModel> userAllGreenhouseArr =
                GDAO.searchBySomeId(userAllGreenhouseId, "id");
        boolean canUseThisName = true;
        // 遍历此用户所有大棚，如果重名且id不同，那么非法
        for (GreenhouseModel tmpGreenhouse : userAllGreenhouseArr) {
            if (tmpGreenhouse.getName().equals(greenhouse.getName())
                    && tmpGreenhouse.getId() != greenhouse.getId()) {
                canUseThisName = false;
                break;
            }
        }
        if (!canUseThisName) {
            return new NormalMessage(false, MyErrorType.GreenhouseNameDuplicate, null);
        }

        // 大棚开启或关闭，会连同操作其内未损坏的传感器群状态，如果大棚损坏，那么将其内除去损坏的传感器群全部关闭
        if (greenhouse.getStatus().equals("close") || greenhouse.getStatus().equals("on")) {
            SGDAO.updateGreenhouseState(greenhouse.getId(), greenhouse.getStatus());
        } else {
            SGDAO.updateGreenhouseState(greenhouse.getId(), "close");
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

        // 把大棚内传感器的阈值设定全部消除
        List<ThresholdModel> thresholdModelList
                = TDAO.searchBySomeId(greenhouseId, "greenhouseId");
        List<Integer> thresholdIdArr = thresholdModelList
                .stream()
                .map(ThresholdModel::getId)
                .collect(Collectors.toList());
        SDAO.updateThreshold(thresholdIdArr);
        TDAO.deleteBySomeId(greenhouseId, "greenhouseId");

        // 把大棚内的设备全部关闭
        List<String> clusterIdArr = SGDAO.searchBySomeId(greenhouseId, "greenhouseId")
                .stream()
                .map(SensorGreenhouseModel::getClusterId)
                .collect(Collectors.toList());
        DDAO.closeAll(clusterIdArr);

        // 清空大棚内所有传感器群的缓存
        Pipe.clearCluster(clusterIdArr);

//        RDAO.deleteBySomeId(greenhouseId,"greenhouseId");


        SGDAO.deleteBySomeId(greenhouseId, "greenhouseId");
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

        if (SDAO.searchBySomeId(clusterId, "clusterId", pwd, "pwd").size() == 0) {
            return new NormalMessage(false, MyErrorType.ClusterVerificationError, null);
        }

        if (SGDAO.searchBySomeId(greenhouseId, "greenhouseId", name, "name").size() != 0) {
            return new NormalMessage(false, MyErrorType.ClusterNameDuplicate, null);
        }

        SGDAO.save(new SensorGreenhouseModel(clusterId, greenhouseId, name));

        return new NormalMessage(true, null, null);
    }

    public NormalMessage getClusterList(String token, Integer greenhouseId) {
        Map<String, Object> userInfo = TokenManager.parseToken(token);
        Integer id = (Integer) userInfo.get("id");

        if (!UGDAO.doesGreenhouseBelongToUser(id, greenhouseId)) {
            return new NormalMessage(false, MyErrorType.GreenhouseAuthorization, null);
        }

        Map<String, List<SensorGreenhouseModel>> message = new HashMap<>();
        message.put("clusterArr", SGDAO.searchBySomeId(greenhouseId, "greenhouseId"));

        return new NormalMessage(true, null, message);
    }
}
