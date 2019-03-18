package com.IOA.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// 这个东西它对于sensor避免去频繁搜索result表
@Component
public class MyPipe {

    // 第一层clusterId，第二层sensorId，第三层value+minimum+maximum
    private ConcurrentMap<String, Map<Integer, Map<String, BigDecimal>>> Sensor2Server;
//    private ConcurrentMap<String, Map<Integer, String>> Device2Server;
    private ConcurrentMap<String, Timestamp> RefreshTime;

    public MyPipe() {
        this.Sensor2Server = new ConcurrentHashMap<>();
//        this.Device2Server = new ConcurrentHashMap<>();
        this.RefreshTime = new ConcurrentHashMap<>();
    }

    public Map<Integer, Map<String, BigDecimal>> getSensor2Server(String clusterId) {

        return this.Sensor2Server.get(clusterId);
    }

    public void setSensor2Server(String clusterId, Map<Integer, Map<String, BigDecimal>> sensorMap) {
        this.Sensor2Server.put(clusterId, sensorMap);
    }

//    public Map<Integer, String> getDevice2Server(String clusterId) {
//        return this.Device2Server.get(clusterId);
//    }
//
//    public void setDevice2Server(String clusterId, Map<Integer, String> deviceMap) {
//        this.Device2Server.put(clusterId, deviceMap);
//    }

    public void clearCluster(String clusterId) {
        this.Sensor2Server.remove(clusterId);
//        this.Device2Server.remove(clusterId);
        this.RefreshTime.remove(clusterId);
    }

    public void clearCluster(List<Object> clusterIdArr) {
        for (Object clusterIdObj : clusterIdArr) {
            String clusterId = (String) clusterIdObj;
            this.Sensor2Server.remove(clusterId);
//            this.Device2Server.remove(clusterId);
            this.RefreshTime.remove(clusterId);
        }
    }


    public Timestamp getRefreshTime(String clusterId) {
        return this.RefreshTime.get(clusterId);
    }

    public void setRefreshTime(String clusterId, Timestamp time) {
        this.RefreshTime.put(clusterId, time);
    }
}
