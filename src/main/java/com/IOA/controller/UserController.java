package com.IOA.controller;

import com.IOA.model.GreenhouseModel;
import com.IOA.model.SensorGreenhouseModel;
import com.IOA.model.ThresholdModel;
import com.IOA.model.UserModel;
import com.IOA.service.*;
import com.IOA.util.MyErrorType;
import com.IOA.vo.NormalMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RequestMapping("/User")
@Controller
public class UserController {

    @Autowired
    private UserService USvc;

    @Autowired
    private GreenhouseService GSvc;

    @Autowired
    private ClusterService CSvc;

    @Autowired
    private TransmissionService TSvc;

    @Autowired
    private SensorService SSvc;

    @RequestMapping(value = "/User-Management/Logoff", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage logoff(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        USvc.logoff(token);
        return new NormalMessage(true, null, null);
    }


    @RequestMapping(value = "/User-Management/Info", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage getSelfInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return USvc.getSelfInfo(token);
    }


    @RequestMapping(value = "/User-Management/Alter", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage alterSelfInfo(@RequestHeader("Authorization") String token,
                                       @RequestBody Map<String, Object> requestMap) {
        String name = (String) requestMap.get("name");
        String oldPwd = (String) requestMap.get("oldPwd");
        String newPwd = (String) requestMap.get("newPwd");

        return USvc.alterSelfInfo(token, name, oldPwd, newPwd);
    }


    @RequestMapping(value = "/Greenhouse-Management/Unbind",
            method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage unbindGreenhouse(@RequestHeader("Authorization") String token,
                                          @RequestBody Map<String, Object> requestMap) {
        Integer greenhouseId = (Integer) requestMap.get("greenhouseId");
        return GSvc.unbind(token, greenhouseId);
    }

    @RequestMapping(value = "/User-Management/Register-Greenhouse", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage greenhouseRegister(@RequestHeader("Authorization") String token,
                                            @RequestBody @Valid GreenhouseModel greenhouse,
                                            Errors errors) {
        if (errors.hasErrors()) {
            return new NormalMessage(false, MyErrorType.WrongParam, null);
        } else {
            return USvc.registerGreenhouse(token, greenhouse);
        }
    }

    @RequestMapping(value = "/User-Management/Greenhouse-List", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage greenhouseList(@RequestHeader("Authorization") String token) {
        return USvc.getGreenhouseList(token);
    }

    @RequestMapping(value = "/Greenhouse-Management/Info", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage greenhouseInfo(@RequestHeader("Authorization") String token,
                                        @RequestBody Map<String, Object> requestMap) {
        Integer greenhouseId = (Integer) requestMap.get("greenhouseId");
        return GSvc.getInfo(token, greenhouseId);
    }

    @RequestMapping(value = "/Greenhouse-Management/Alter", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage alterGreenhouse(@RequestHeader("Authorization") String token,
                                         @RequestBody @Valid GreenhouseModel greenhouse,
                                         Errors errors) {
        if (errors.hasErrors()) {
            return new NormalMessage(false, MyErrorType.WrongParam, null);
        } else {
            return GSvc.alterInfo(token, greenhouse);
        }
    }

    @RequestMapping(value = "/Greenhouse-Management/Bind-Cluster", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage bindCluster(@RequestHeader("Authorization") String token,
                                     @RequestBody Map<String, Object> requestMap) {
        String clusterId = (String) requestMap.get("clusterId");
        Integer greenhouseId = (Integer) requestMap.get("greenhouseId");
        String pwd = (String) requestMap.get("pwd");
        String name = (String) requestMap.get("name");
        return GSvc.bindCluster(token, clusterId, greenhouseId, pwd, name);
    }

    @RequestMapping(value = "/Greenhouse-Management/Cluster-List", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage getClusterList(@RequestHeader("Authorization") String token,
                                        @RequestBody Map<String, Object> requestMap) {
        Integer greenhouseId = (Integer) requestMap.get("greenhouseId");
        return GSvc.getClusterList(token, greenhouseId);
    }

    @RequestMapping(value = "/Cluster-Management/Unbind", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage unbindCluster(@RequestHeader("Authorization") String token,
                                       @RequestBody Map<String, Object> requestMap) {
        String clusterId = (String) requestMap.get("clusterId");
        Integer greenhouseId = (Integer) requestMap.get("greenhouseId");
        return CSvc.unbind(token, clusterId, greenhouseId);
    }

    @RequestMapping(value = "/Cluster-Management/Info", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage clusterInfo(@RequestHeader("Authorization") String token,
                                     @RequestBody Map<String, Object> requestMap) {
        String clusterId = (String) requestMap.get("clusterId");
        return CSvc.getInfo(token, clusterId);
    }

    @RequestMapping(value = "/Cluster-Management/Alter", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage alterCluster(@RequestHeader("Authorization") String token,
                                      @RequestBody @Valid SensorGreenhouseModel cluster,
                                      Errors errors) {
        if (errors.hasErrors()) {
            return new NormalMessage(false, MyErrorType.WrongParam, null);
        } else {
            return CSvc.alterInfo(token, cluster);
        }
    }

    @RequestMapping(value = "/Cluster-Management/Status", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage clusterStatus(@RequestHeader("Authorization") String token,
                                       @RequestBody Map<String, Object> requestMap) {
        String clusterId = (String) requestMap.get("clusterId");
        return CSvc.getStatus(token, clusterId);
    }

    @RequestMapping(value = "/Cluster-Management/Order", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage ClusterOrder(@RequestBody Map<String, Object> requestMap) {
        Integer deviceId = (Integer) requestMap.get("id");
        String status = (String) requestMap.get("status");
        return TSvc.sendOrder(deviceId, status);
    }

    @RequestMapping(value = "/Sensor-Management/Register-Threshold", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage registerThreshold(@RequestHeader("Authorization") String token,
                                           @RequestBody @Valid ThresholdModel threshold,
                                           Errors errors) {
        if (errors.hasErrors()) {
            return new NormalMessage(false, MyErrorType.WrongParam, null);
        } else {
            return SSvc.registerThreshold(token, threshold);
        }
    }

    @RequestMapping(value = "/Sensor-Management/Info", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage sensorInfo(@RequestHeader("Authorization") String token,
                                    @RequestBody Map<String, Object> requestMap) {
        Integer sensorId = (Integer) requestMap.get("id");
        return SSvc.getInfo(token, sensorId);
    }

    @RequestMapping(value = "/Sensor-Management/Latest-Val", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage sensorLatestVal(@RequestHeader("Authorization") String token,
                                         @RequestBody Map<String, Object> requestMap) {
        Integer sensorId = (Integer) requestMap.get("id");
        return SSvc.getLatestVal(token, sensorId);
    }

    @RequestMapping(value = "/Sensor-Management/History-Val", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage sensorHistoryVal(@RequestHeader("Authorization") String token,
                                          @RequestBody Map<String, Object> requestMap) {
        Integer sensorId = (Integer) requestMap.get("id");
        return SSvc.getHistoryVal(token, sensorId);
    }

    @RequestMapping(value = "/Sensor-Management/Threshold-List", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage getSensorThresholdList(@RequestHeader("Authorization") String token,
                                                @RequestBody Map<String, Object> requestMap) {
        Integer sensorId = (Integer) requestMap.get("id");
        return SSvc.getThresholdList(token, sensorId);
    }

    @RequestMapping(value = "/Sensor-Management/Select-Threshold", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage selectSensorThreshold(@RequestHeader("Authorization") String token,
                                               @RequestBody Map<String, Object> requestMap) {
        Integer sensorId = (Integer) requestMap.get("sensorId");
        Integer thresholdId = (Integer) requestMap.get("thresholdId");
        return SSvc.selectThreshold(token, sensorId, thresholdId);
    }

    @RequestMapping(value = "/Sensor-Management/Unbind-Threshold", method = RequestMethod.POST)
    @ResponseBody
    public NormalMessage unbindSensorThreshold(@RequestHeader("Authorization") String token,
                                               @RequestBody Map<String, Object> requestMap) {
        Integer sensorId = (Integer) requestMap.get("sensorId");
        Integer thresholdId = (Integer) requestMap.get("thresholdId");
        return SSvc.unbindThreshold(token, sensorId, thresholdId);
    }
}