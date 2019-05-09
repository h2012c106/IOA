package com.IOA.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "greenhouse_cluster", schema = "ioa", catalog = "")
@IdClass(GreenhouseClusterModelPK.class)
public class GreenhouseClusterModel {
    private String clusterId;
    private int greenhouseId;
    private String name;
    private String location;

    public GreenhouseClusterModel() {
    }

    public GreenhouseClusterModel(String clusterId, int greenhouseId, String name, String location) {
        this.clusterId = clusterId;
        this.greenhouseId = greenhouseId;
        this.name = name;
        this.location = location;
    }

    @Id
    @Column(name = "cluster_id")
    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    @Id
    @Column(name = "greenhouse_id")
    public int getGreenhouseId() {
        return greenhouseId;
    }

    public void setGreenhouseId(int greenhouseId) {
        this.greenhouseId = greenhouseId;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "location")
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GreenhouseClusterModel that = (GreenhouseClusterModel) o;
        return greenhouseId == that.greenhouseId &&
                Objects.equals(clusterId, that.clusterId) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clusterId, greenhouseId, name);
    }
}
