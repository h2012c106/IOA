package com.IOA.model;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Objects;

@Entity
@DynamicInsert
@Table(name = "sensor", schema = "ioa", catalog = "")
public class SensorModel {
    private int id;
    private String pwd;
    private String type;
    private String unit;
    private int thresholdId;
    @Length(min = 17, max = 17)
    private String clusterId;
    private int innerId;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "pwd")
    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Basic
    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "unit")
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Basic
    @Column(name = "threshold_id", columnDefinition = "int DEFAULT -1")
    public int getThresholdId() {
        return thresholdId;
    }

    public void setThresholdId(int thresholdId) {
        this.thresholdId = thresholdId;
    }

    @Basic
    @Column(name = "cluster_id")
    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
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
        SensorModel that = (SensorModel) o;
        return id == that.id &&
                thresholdId == that.thresholdId &&
                innerId == that.innerId &&
                Objects.equals(pwd, that.pwd) &&
                Objects.equals(type, that.type) &&
                Objects.equals(unit, that.unit) &&
                Objects.equals(clusterId, that.clusterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pwd, type, unit, thresholdId, clusterId, innerId);
    }
}
