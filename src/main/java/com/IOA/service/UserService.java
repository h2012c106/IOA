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
public class UserService {

    @Autowired
    UserDAO UDAO;

    @Autowired
    SensorDAO SDAO;

    @Autowired
    DeviceDAO DDAO;

    @Autowired
    ThresholdDAO TDAO;

    @Autowired
    ResultDAO RDAO;

    @Autowired
    SensorGreenhouseDAO SGDAO;

    @Autowired
    UserGreenhouseDAO UGDAO;

    @Autowired
    GreenhouseDAO GDAO;

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
        boolean canUseThisName = true;
        if (!name.equals(user.getName())) {
            List<UserModel> useThisNameUserArr = UDAO.searchBySomeId(name, "name");
            for (UserModel tmpUser : useThisNameUserArr) {
                if (tmpUser.getId() != id) {
                    canUseThisName = false;
                    break;
                }
            }
        }
        if (!canUseThisName) {
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


    public void logoff(String token) {
        Map<String, Object> userInfo = TokenManager.parseToken(token);
        Object id = userInfo.get("id");
        // 方案一：把他与他的大棚+传感器解绑，再把他除名，结果->人除名，但是传感器与大棚依然在数据库中存在，
        // 且不取消之间的绑定关系，但仅可以被管理员查看
//        UGDAO.deleteBySomeId(tmpList,"userId");
//        USDAO.deleteBySomeId(tmpList,"userId");
//        UDAO.deleteBySomeId(tmpList,"id");

        // 方案二：把他与他的大棚+传感器解绑，再把他除名，结果->人除名，但是传感器与大棚依然在数据库中存在，
        // 并取消之间的绑定关系，但仅可以被管理员查看

        // 方案三：把他与他的大棚+传感器解绑，清除其大棚数据，再把他除名，结果->人除名，
        // 仅保留传感器数据（比较合理）
        List<UserGreenhouseModel> abandonedGreenhouseArr = UGDAO.searchBySomeId(id, "userId");
        List<Object> abandonedGreenhouseIdArr = abandonedGreenhouseArr.stream()
                .map(UserGreenhouseModel::getGreenhouseId)
                .collect(Collectors.toList());

        // 把所有大棚内的设备全部关闭
        List<String> clusterIdArr = SGDAO.searchBySomeId(abandonedGreenhouseIdArr, "greenhouseId")
                .stream()
                .map(SensorGreenhouseModel::getClusterId)
                .collect(Collectors.toList());
        DDAO.closeAll(clusterIdArr);

        // 清空所有大棚内所有传感器群的缓存
        Pipe.clearCluster(clusterIdArr);

        UGDAO.deleteBySomeId(id, "userId");
        SGDAO.deleteBySomeId(abandonedGreenhouseIdArr, "greenhouseId");

        // 把所有大棚内传感器的阈值设定全部消除
        List<ThresholdModel> thresholdModelList
                =TDAO.searchBySomeId(abandonedGreenhouseIdArr,"greenhouseId");
        List<Integer> thresholdIdArr=thresholdModelList
                .stream()
                .map(ThresholdModel::getId)
                .collect(Collectors.toList());
        SDAO.updateThreshold(thresholdIdArr);
        TDAO.deleteBySomeId(abandonedGreenhouseIdArr,"greenhouseId");

//        RDAO.deleteBySomeId(greenhouseId,"greenhouseId");

        GDAO.deleteBySomeId(abandonedGreenhouseIdArr, "id");
        UDAO.deleteBySomeId(id, "id");

        // 方案四：把他与他的大棚+传感器解绑，且删除其大棚、传感器数据，再把他除名，
        // 结果->与此人相关的一切东西完全消失
    }

    public NormalMessage registerGreenhouse(String token, GreenhouseModel greenhouse) {
        Map<String, Object> userInfo = TokenManager.parseToken(token);
        Integer id = (Integer) userInfo.get("id");

        // 名下大棚不可重名
        List<UserGreenhouseModel> userAllGreenhouse = UGDAO.searchBySomeId(id, "userId");
        List<Object> userAllGreenhouseId = userAllGreenhouse.stream()
                .map(UserGreenhouseModel::getGreenhouseId)
                .collect(Collectors.toList());
        List<GreenhouseModel> sameNameGreenhouseArr =
                GDAO.searchBySomeId(userAllGreenhouseId, "id");
        boolean canUseThisName = true;
        for (GreenhouseModel tmpGreenhouse : sameNameGreenhouseArr) {
            if (tmpGreenhouse.getName().equals(greenhouse.getName())) {
                canUseThisName = false;
                break;
            }
        }
        if (!canUseThisName) {
            return new NormalMessage(false, MyErrorType.GreenhouseNameDuplicate, null);
        }

        GDAO.save(greenhouse);
        UGDAO.save(new UserGreenhouseModel(id, greenhouse.getId()));

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
}
