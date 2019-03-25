package com.IOA.controller;


import javax.validation.Valid;

import com.IOA.util.MyErrorType;
import com.IOA.dto.NormalMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.IOA.service.AccessService;
import com.IOA.model.UserModel;

@RequestMapping("/Access")
@Controller
public class AccessController {

    @Autowired
    private AccessService ASvc;

    @RequestMapping(value = "/Register", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage register(@RequestBody @Valid UserModel user, Errors errors) {
        if (errors.hasErrors()) {
            return new NormalMessage(false, MyErrorType.WrongParam, null);
        } else {
            return ASvc.register(user);
        }
    }

    @RequestMapping(value = "/Login", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage login(@RequestBody @Valid UserModel user, Errors errors) {
        if (errors.hasErrors()) {
            return new NormalMessage(false, MyErrorType.WrongParam, null);
        } else {
            return ASvc.login(user);
        }
    }

//    @RequestMapping(value = "/register", method = RequestMethod.POST)
//    @ResponseBody
//    public AccessMessage register(HttpServletRequest req) throws IOException{
//        AccessMessage res = new AccessMessage();
//
//        String jsonString = getBodyString(req.getReader());
//        JSONObject jbJsonObject = new JSONObject().fromObject(jsonString);
//        System.out.println(jbJsonObject);
//        UserModel user=(UserModel) JSONObject.toBean(jbJsonObject, UserModel.class);
//        System.out.println(user.getName() + user.getPwd() + user.getUserType());
//        String token = ASvc.register(user);
//        if (token == null) {
//            res.setSuccess(false);
//            res.setError("用户名已存在");
//        } else {
//            res.setSuccess(true);
//            res.setToken(token);
//        }
//
//        return res;
//    }
//
//    public String getBodyString(BufferedReader br) {
//        String inputLine;
//        String str = "";
//        try {
//            while ((inputLine = br.readLine()) != null) {
//                str += inputLine;
//            }
//            br.close();
//        } catch (IOException e) {
//            System.out.println("IOException: " + e);
//        }
//        return str;
//    }
}


