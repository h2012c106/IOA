package com.IOA.service;

import com.IOA.dao.*;
import com.IOA.model.*;
import com.IOA.util.MD5Manager;
import com.IOA.util.MyErrorType;
import com.IOA.util.MyPipe;
import com.IOA.util.TokenManager;
import com.IOA.dto.NormalMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

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

    public NormalMessage getSelfInfo(String token) {
        Map<String, Object> userInfo = TokenManager.parseToken(token);
        List<UserModel> userArr = UDAO.searchBySomeId(userInfo.get("id"), "id");
        if (userArr.size() == 0) {
            return new NormalMessage(false, MyErrorType.UserUnexist, null);
        } else {
            return new NormalMessage(true, null, userArr.get(0));
        }
    }

    public NormalMessage alterSelfInfo(String token, String name,
                                       String oldPwd, String newPwd) {
        Map<String, Object> userInfo = TokenManager.parseToken(token);
        Integer id = (Integer) userInfo.get("id");
        UserModel user = UDAO.searchBySomeId(id, "id").get(0);

        // 找出用这个名字的用户，如果是自己就忽略，是别人的话就说明重名，不可用
        if (UDAO.isNameDuplicate(name, id)) {
            return new NormalMessage(false, MyErrorType.UserNameDuplicate, null);
        }

        if (!MD5Manager.verify(oldPwd, user.getPwd())) {
            return new NormalMessage(false, MyErrorType.UserPasswordWrong, null);
        }

        user.setPwd(MD5Manager.encode(newPwd));
        user.setName(name);
        return UDAO.userUpdate(user)
                ? new NormalMessage(true, null, null)
                : new NormalMessage(false, MyErrorType.UpdateError, null);
    }


    public NormalMessage logoff(String token) {
        Map<String, Object> userInfo = TokenManager.parseToken(token);
        Object id = userInfo.get("id");

        // 找到此人名下的所有大棚
        List<UserGreenhouseModel> abandonedGreenhouseArr
                = UGDAO.searchBySomeId(id, "userId");
        List<Object> abandonedGreenhouseIdArr = abandonedGreenhouseArr.stream()
                .map(UserGreenhouseModel::getGreenhouseId)
                .collect(Collectors.toList());
        // 找到所有大棚内所有传感器群
        List<GreenhouseClusterModel> abandonedClusterArr
                = GCDAO.searchBySomeId(abandonedGreenhouseIdArr, "greenhouseId");
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

        // 删除传感器对应的阈值对以及其目前选择的阈值
        SeDAO.deleteBySomeId(abandonedSensorIdArr, "sensorId");
        STDAO.deleteBySomeId(abandonedSensorIdArr, "sensorId");
        TDAO.deleteBySomeId(abandonedThresholdIdArr, "id");

        // 将所有设备全部关闭
        DDAO.closeAll(abandonedDeviceIdArr);

        // 将缓存内所有结果清除
        Pipe.clearCluster(abandonedClusterIdArr);

        // 将所有传感器群解绑并且把没问题的关掉
        GCDAO.deleteBySomeId(abandonedGreenhouseIdArr, "greenhouseId");
        CDAO.updateStatus(abandonedClusterIdArr, "close");

        // 将所有大棚解绑并删除
        UGDAO.deleteBySomeId(id, "userId");
        GDAO.deleteBySomeId(abandonedGreenhouseIdArr, "id");

        // 删除此用户
        UDAO.deleteBySomeId(id, "id");

        return new NormalMessage(true, null, null);
    }

    public NormalMessage registerGreenhouse(String token, GreenhouseModel greenhouse) {
        Map<String, Object> userInfo = TokenManager.parseToken(token);
        Integer id = (Integer) userInfo.get("id");

        // 名下大棚不可重名
        List<UserGreenhouseModel> userAllGreenhouse = UGDAO.searchBySomeId(id, "userId");
        List<Object> userAllGreenhouseId = userAllGreenhouse.stream()
                .map(UserGreenhouseModel::getGreenhouseId)
                .collect(Collectors.toList());
        if (GDAO.isNameDuplicate(userAllGreenhouseId, greenhouse.getName())) {
            return new NormalMessage(false, MyErrorType.GreenhouseNameDuplicate, null);
        }

        Integer newId = GDAO.saveBackId(greenhouse);
        UGDAO.save(new UserGreenhouseModel(id, newId));

        return new NormalMessage(true, null, null);
    }

    public NormalMessage getGreenhouseList(String token) {
        Map<String, Object> userInfo = TokenManager.parseToken(token);
        Object id = userInfo.get("id");

        List<UserGreenhouseModel> greenhouseUserArr = UGDAO.searchBySomeId(id, "userId");
        List<Object> greenhouseIdArr = greenhouseUserArr.stream()
                .map(UserGreenhouseModel::getGreenhouseId)
                .collect(Collectors.toList());
        Map<String, List<GreenhouseModel>> message = new HashMap<>();
        message.put("greenhouseArr", GDAO.searchBySomeId(greenhouseIdArr, "id"));
        return new NormalMessage(true, null, message);
    }


    public NormalMessage getList() {
        List<UserModel> userArr = UDAO.searchAll();
        Map<String, List<UserModel>> message = new HashMap<>();
        message.put("userArr", userArr);
        return new NormalMessage(true, null, message);
    }

    public NormalMessage getSomeoneInfo(Integer id) {
        List<UserModel> userArr = UDAO.searchBySomeId(id, "id");
        if (userArr.size() == 0) {
            return new NormalMessage(false, MyErrorType.UserUnexist, null);
        } else {
            return new NormalMessage(true, null, userArr.get(0));
        }
    }

    public NormalMessage getSomeoneGreenhouseList(Integer id) {
        List<UserGreenhouseModel> greenhouseUserArr
                = UGDAO.searchBySomeId(id, "userId");
        List<Object> greenhouseIdArr = greenhouseUserArr.stream()
                .map(UserGreenhouseModel::getGreenhouseId)
                .collect(Collectors.toList());
        Map<String, List<GreenhouseModel>> message = new HashMap<>();
        message.put("greenhouseArr", GDAO.searchBySomeId(greenhouseIdArr, "id"));
        return new NormalMessage(true, null, message);
    }

    public NormalMessage alterSomeonePwd(Integer id, String newPwd) {
        return UDAO.adminUpdate(id, newPwd)
                ? new NormalMessage(true, null, null)
                : new NormalMessage(false, MyErrorType.UpdateError, null);
    }

    public NormalMessage deleteSomeone(Integer id) {
        // 找到此人名下的所有大棚
        List<UserGreenhouseModel> abandonedGreenhouseArr
                = UGDAO.searchBySomeId(id, "userId");
        List<Object> abandonedGreenhouseIdArr = abandonedGreenhouseArr.stream()
                .map(UserGreenhouseModel::getGreenhouseId)
                .collect(Collectors.toList());
        // 找到所有大棚内所有传感器群
        List<GreenhouseClusterModel> abandonedClusterArr
                = GCDAO.searchBySomeId(abandonedGreenhouseIdArr, "greenhouseId");
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

        // 删除传感器对应的阈值对以及其目前选择的阈值
        SeDAO.deleteBySomeId(abandonedSensorIdArr, "sensorId");
        STDAO.deleteBySomeId(abandonedSensorIdArr, "sensorId");
        TDAO.deleteBySomeId(abandonedThresholdIdArr, "id");

        // 将所有设备全部关闭
        DDAO.closeAll(abandonedDeviceIdArr);

        // 将缓存内所有结果清除
        Pipe.clearCluster(abandonedClusterIdArr);

        // 将所有传感器群解绑并且把没问题的关掉
        GCDAO.deleteBySomeId(abandonedGreenhouseIdArr, "greenhouseId");
        CDAO.updateStatus(abandonedClusterIdArr, "close");

        // 将所有大棚解绑并删除
        UGDAO.deleteBySomeId(id, "userId");
        GDAO.deleteBySomeId(abandonedGreenhouseIdArr, "id");

        // 删除此用户
        UDAO.deleteBySomeId(id, "id");

        return new NormalMessage(true, null, null);
    }
}
