package com.IOA.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cluster_sensor", schema = "ioa", catalog = "")
@IdClass(ClusterSensorModelPK.class)
public class ClusterSensorModel {
    private String clusterId;
    private int sensorId;
    private int innerId;

    @Id
    @Column(name = "cluster_id")
    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    @Id
    @Column(name = "sensor_id")
    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    @Basic
    @Column(name = "inner_id")
    public int getInnerId() {
        return innerId;
    }

    public void setInnerId(int innerId) {
        this.innerId = innerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClusterSensorModel that = (ClusterSensorModel) o;
        return sensorId == that.sensorId &&
                innerId == that.innerId &&
                Objects.equals(clusterId, that.clusterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clusterId, sensorId, innerId);
    }
}
