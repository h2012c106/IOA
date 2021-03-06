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
    private ConcurrentMap<String, Timestamp> RefreshTime;
    public Boolean On = true;

    public MyPipe() {
        this.Sensor2Server = new ConcurrentHashMap<>();
//        this.Device2Server = new ConcurrentHashMap<>();
        this.RefreshTime = new ConcurrentHashMap<>();
    }

    public Map<Integer, Map<String, BigDecimal>> getSensor2Server(String clusterId) {

        return this.On ? this.Sensor2Server.get(clusterId) : null;
    }

    public void setSensor2Server(String clusterId, Map<Integer, Map<String, BigDecimal>> sensorMap) {
        if (this.On)
            this.Sensor2Server.put(clusterId, sensorMap);
    }

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
        return this.On ? this.RefreshTime.get(clusterId) : null;
    }

    public void setRefreshTime(String clusterId, Timestamp time) {
        if (this.On)
            this.RefreshTime.put(clusterId, time);
    }
}
