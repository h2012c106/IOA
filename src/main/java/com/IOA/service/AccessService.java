package com.IOA.service;

import com.IOA.dao.*;
import com.IOA.util.MyErrorType;
import com.IOA.vo.NormalMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.IOA.model.UserModel;
import com.IOA.model.UserGreenhouseModel;
import com.IOA.util.TokenManager;
import com.IOA.util.MD5Manager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccessService {

    @Autowired
    private UserDAO UDAO;

    @Autowired
    private GreenhouseDAO GDAO;

    @Autowired
    private UserGreenhouseDAO UGDAO;

    @Autowired
    private SensorGreenhouseDAO SGDAO;

    public NormalMessage register(UserModel user) {
        // 查找数据库中是否存在此用户名，存在则打回，否则通过
        if (UDAO.searchBySomeId(user.getName(), "name").size() == 0) {
            user.setPwd(MD5Manager.encode(user.getPwd()));
            Integer newId = UDAO.saveBackId(user);
            user.setId(newId);
            return new NormalMessage(false, MyErrorType.UserNameDuplicate, null);
        } else {
            Map<String, String> message = new HashMap<>();
            message.put("token", TokenManager.generateToken(user));
            return new NormalMessage(true, null, message);
        }
    }

    public NormalMessage login(UserModel user) {
        List<UserModel> tmpUserArr = UDAO.searchBySomeId(user.getName(), "name");
        if (tmpUserArr.size() == 0) {
            return new NormalMessage(false, MyErrorType.UserNameUnexist, null);
        }
        // 因为用户在登陆时传入的信息中不包括id，也可能不包括用户类型，所以要使用数据库表中的的内容
        if (MD5Manager.verify(user.getPwd(), tmpUserArr.get(0).getPwd())) {
            Map<String, String> message = new HashMap<>();
            message.put("token", TokenManager.generateToken(tmpUserArr.get(0)));
            return new NormalMessage(true, null, message);
        } else {
            return new NormalMessage(false, MyErrorType.UserPasswordWrong, null);
        }
    }
}