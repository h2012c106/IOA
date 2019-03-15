package com.IOA.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class SensorConfig {
    public static final int PORT;
    public static final List<Map<String, String>> DeviceArr = new ArrayList<>();
    public static final List<Map<String, String>> SensorArr = new ArrayList<>();

    static {

        String cfgPath = SensorConfig.class.getClassLoader().getResource("META-INF/sensor.cfg.json").toString();
        cfgPath = cfgPath.replace("\\", "/");
        if (cfgPath.contains(":")) {
            cfgPath = cfgPath.replace("file:/", "");// 2
        }

        String input = null;
        try {
            input = FileUtils.readFileToString(new File(cfgPath), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSONObject.fromObject(input);

        if (jsonObject != null) {
            System.out.println("读取配置文件成功");

            PORT = jsonObject.getInt("port");

            JSONArray sensorJsonArr = jsonObject.getJSONArray("sensorArr");
            for (Object tmpObj : sensorJsonArr) {
                JSONObject tmpJsonObj = JSONObject.fromObject(tmpObj);
                SensorArr.add(tmpJsonObj);
            }

            JSONArray deviceJsonArr = jsonObject.getJSONArray("deviceArr");
            for (Object tmpObj : deviceJsonArr) {
                JSONObject tmpJsonObj = JSONObject.fromObject(tmpObj);
                DeviceArr.add(tmpJsonObj);
            }
            System.out.println(SensorArr);
            System.out.println(DeviceArr);
        } else { // 手动录入
            System.out.println("读取配置文件失败");

            PORT = 8888;

            Map<String, String> tmpMap;

            // 传感器
            tmpMap = new HashMap<>();
            tmpMap.put("type", "土壤湿度");
            tmpMap.put("unit", "%");
            tmpMap.put("innerId", "0");
            SensorArr.add(tmpMap);

            tmpMap = new HashMap<>();
            tmpMap.put("type", "土壤温度");
            tmpMap.put("unit", "℃");
            tmpMap.put("innerId", "1");
            SensorArr.add(tmpMap);

            tmpMap = new HashMap<>();
            tmpMap.put("type", "土壤电导率");
            tmpMap.put("unit", "um/s");
            tmpMap.put("innerId", "2");
            SensorArr.add(tmpMap);

            tmpMap = new HashMap<>();
            tmpMap.put("type", "土壤EC值");
            tmpMap.put("unit", "um/s");
            tmpMap.put("innerId", "3");
            SensorArr.add(tmpMap);

            tmpMap = new HashMap<>();
            tmpMap.put("type", "空气湿度");
            tmpMap.put("unit", "%");
            tmpMap.put("innerId", "4");
            SensorArr.add(tmpMap);

            tmpMap = new HashMap<>();
            tmpMap.put("type", "空气温度");
            tmpMap.put("unit", "℃");
            tmpMap.put("innerId", "5");
            SensorArr.add(tmpMap);

            tmpMap = new HashMap<>();
            tmpMap.put("type", "二氧化碳浓度");
            tmpMap.put("unit", "ppm");
            tmpMap.put("innerId", "6");
            SensorArr.add(tmpMap);

            tmpMap = new HashMap<>();
            tmpMap.put("type", "光照强度");
            tmpMap.put("unit", "Lux");
            tmpMap.put("innerId", "7");
            SensorArr.add(tmpMap);

            tmpMap = new HashMap<>();
            tmpMap.put("type", "土壤PH值");
            tmpMap.put("unit", "");
            tmpMap.put("innerId", "8");
            SensorArr.add(tmpMap);

            tmpMap = new HashMap<>();
            tmpMap.put("type", "压强");
            tmpMap.put("unit", "Pa");
            tmpMap.put("innerId", "9");
            SensorArr.add(tmpMap);

            tmpMap = new HashMap<>();
            tmpMap.put("type", "风速");
            tmpMap.put("unit", "m/s");
            tmpMap.put("innerId", "10");
            SensorArr.add(tmpMap);

            tmpMap = new HashMap<>();
            tmpMap.put("type", "氧气浓度");
            tmpMap.put("unit", "%");
            tmpMap.put("innerId", "11");
            SensorArr.add(tmpMap);

            // 设备
            tmpMap = new HashMap<>();
            tmpMap.put("name", "灯泡");
            tmpMap.put("nickname", "lc");
            tmpMap.put("status2order", "0:0;1:1");
            DeviceArr.add(tmpMap);

            tmpMap = new HashMap<>();
            tmpMap.put("name", "温控");
            tmpMap.put("nickname", "tc");
            tmpMap.put("status2order", "0:4;1:6;2:5");
            DeviceArr.add(tmpMap);

            tmpMap = new HashMap<>();
            tmpMap.put("name", "水泵");
            tmpMap.put("nickname", "wc");
            tmpMap.put("status2order", "0:3;1:2");
            DeviceArr.add(tmpMap);

            tmpMap = new HashMap<>();
            tmpMap.put("name", "风扇");
            tmpMap.put("nickname", "fc");
            tmpMap.put("status2order", "0:8;1:7");
            DeviceArr.add(tmpMap);
        }
    }

    public static String TranslateOrder(String nickname, String status) {
        for (Map<String, String> tmpMap : DeviceArr) {
            if (tmpMap.get("nickname").equals(nickname)) {
                return translateStatus2Order(tmpMap.get("status2order")).get(status);
            }
        }
        return null;
    }

    private static Map<String, String> translateStatus2Order(String status2order) {
        Map<String, String> res = new HashMap<>();
        String[] pairArr = status2order.split(";");
        for (String pair : pairArr) {
            String[] spl = pair.split(":");
            res.put(spl[0], spl[1]);
        }
        return res;
    }
}
