package com.IOA.model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class SensorGreenhouseModelPK implements Serializable {
    private String clusterId;
    private int greenhouseId;

    @Column(name = "cluster_id")
    @Id
    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    @Column(name = "greenhouse_id")
    @Id
    public int getGreenhouseId() {
        return greenhouseId;
    }

    public void setGreenhouseId(int greenhouseId) {
        this.greenhouseId = greenhouseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorGreenhouseModelPK that = (SensorGreenhouseModelPK) o;
        return greenhouseId == that.greenhouseId &&
                Objects.equals(clusterId, that.clusterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clusterId, greenhouseId);
    }
}
