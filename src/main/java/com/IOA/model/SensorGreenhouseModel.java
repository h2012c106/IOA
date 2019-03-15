package com.IOA.model;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "sensor_greenhouse", schema = "ioa", catalog = "")
@DynamicInsert
@IdClass(SensorGreenhouseModelPK.class)
public class SensorGreenhouseModel {

    @NotNull
    @Length(min = 17, max = 17)
    private String clusterId;

    private int greenhouseId;

    @NotNull
    private String name;

    @NotNull
    private String status;

    public SensorGreenhouseModel() {
    }

    public SensorGreenhouseModel(String clusterId, int greenhouseId, String name) {
        this.clusterId = clusterId;
        this.greenhouseId = greenhouseId;
        this.name = name;
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
    @Column(name = "status", columnDefinition = "enum('error','on','close') DEFAULT 'on'")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorGreenhouseModel that = (SensorGreenhouseModel) o;
        return greenhouseId == that.greenhouseId &&
                Objects.equals(clusterId, that.clusterId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clusterId, greenhouseId, name, status);
    }
}
