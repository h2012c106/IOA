package com.IOA.model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class ClusterDeviceModelPK implements Serializable {
    private String clusterId;
    private int deviceId;

    @Column(name = "cluster_id")
    @Id
    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    @Column(name = "device_id")
    @Id
    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClusterDeviceModelPK that = (ClusterDeviceModelPK) o;
        return deviceId == that.deviceId &&
                Objects.equals(clusterId, that.clusterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clusterId, deviceId);
    }
}
