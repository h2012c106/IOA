package com.IOA.model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class ClusterSensorModelPK implements Serializable {
    private String clusterId;
    private int sensorId;

    @Column(name = "cluster_id")
    @Id
    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    @Column(name = "sensor_id")
    @Id
    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClusterSensorModelPK that = (ClusterSensorModelPK) o;
        return sensorId == that.sensorId &&
                Objects.equals(clusterId, that.clusterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clusterId, sensorId);
    }
}
