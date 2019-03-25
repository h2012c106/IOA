package com.IOA.controller;

import com.IOA.model.ClusterModel;
import com.IOA.service.ClusterService;
import com.IOA.service.GreenhouseService;
import com.IOA.service.UserService;
import com.IOA.util.MyErrorType;
import com.IOA.dto.NormalMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RequestMapping("/Admin")
@Controller
public class AdminController {

    @Autowired
    UserService USvc;

    @Autowired
    GreenhouseService GSvc;

    @Autowired
    ClusterService CSvc;

    @RequestMapping(value = "/User-Management/List", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage userList() {
        return USvc.getList();
    }

    @RequestMapping(value = "/User-Management/Info", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage userInfo(@RequestBody Map<String, Object> requestMap) {
        Integer userId = (Integer) requestMap.get("id");
        return USvc.getSomeoneInfo(userId);
    }

    @RequestMapping(value = "/User-Management/Greenhouse-List", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage greenhouseList(@RequestBody Map<String, Object> requestMap) {
        Integer userId = (Integer) requestMap.get("id");
        return USvc.getSomeoneGreenhouseList(userId);
    }

    @RequestMapping(value = "/User-Management/Alter-Password", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage alterPwd(@RequestBody Map<String, Object> requestMap) {
        Integer userId = (Integer) requestMap.get("id");
        String newPwd = (String) requestMap.get("pwd");
        return USvc.alterSomeonePwd(userId, newPwd);
    }

    @RequestMapping(value = "/User-Management/Delete", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage deleteUser(@RequestBody Map<String, Object> requestMap) {
        Integer userId = (Integer) requestMap.get("id");
        return USvc.deleteSomeone(userId);
    }

    @RequestMapping(value = "/Greenhouse-Management/Info", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage greenhouseInfo(@RequestBody Map<String, Object> requestMap) {
        Integer greenhouseId = (Integer) requestMap.get("greenhouseId");
        return GSvc.getSomeGreenhouseInfo(greenhouseId);
    }

    @RequestMapping(value = "/Greenhouse-Management/Cluster-List", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage greenhouseClusterList(@RequestBody Map<String, Object> requestMap) {
        Integer greenhouseId = (Integer) requestMap.get("greenhouseId");
        return GSvc.getSomeGreenhouseClusterList(greenhouseId);
    }

    @RequestMapping(value = "/Cluster-Management/Register", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage registerCluster(@RequestBody @Valid ClusterModel cluster, Errors errors) {
        if (errors.hasErrors()) {
            return new NormalMessage(false, MyErrorType.WrongParam, null);
        } else {
            return CSvc.register(cluster);
        }
    }

    @RequestMapping(value = "/Cluster-Management/List", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage clusterList() {
        return CSvc.getList();
    }

    @RequestMapping(value = "/Cluster-Management/Info", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage clusterInfo(@RequestBody Map<String, Object> requestMap) {
        String clusterId = (String) requestMap.get("clusterId");
        return CSvc.getInfo(clusterId);
    }

    @RequestMapping(value = "/Cluster-Management/Alter-Password", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage alterClusterPwd(@RequestBody @Valid ClusterModel cluster, Errors errors) {
        if (errors.hasErrors()) {
            return new NormalMessage(false, MyErrorType.WrongParam, null);
        } else {
            return CSvc.alterPwd(cluster);
        }
    }

    @RequestMapping(value = "/Cluster-Management/Member", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage clusterMember(@RequestBody Map<String, Object> requestMap) {
        String clusterId = (String) requestMap.get("clusterId");
        return CSvc.getMember(clusterId);
    }

    @RequestMapping(value = "/Cluster-Management/Delete", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage deleteCluster(@RequestBody Map<String, Object> requestMap) {
        String clusterId = (String) requestMap.get("clusterId");
        return CSvc.delete(clusterId);
    }

}
