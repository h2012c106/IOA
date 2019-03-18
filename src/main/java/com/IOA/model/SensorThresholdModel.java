package com.IOA.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "sensor_threshold", schema = "ioa", catalog = "")
@IdClass(SensorThresholdModelPK.class)
public class SensorThresholdModel {
    private int sensorId;
    private int thresholdId;

    public SensorThresholdModel() {
    }

    public SensorThresholdModel(int sensorId, int thresholdId) {
        this.sensorId = sensorId;
        this.thresholdId = thresholdId;
    }

    @Id
    @Column(name = "sensor_id")
    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    @Id
    @Column(name = "threshold_id")
    public int getThresholdId() {
        return thresholdId;
    }

    public void setThresholdId(int thresholdId) {
        this.thresholdId = thresholdId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorThresholdModel that = (SensorThresholdModel) o;
        return sensorId == that.sensorId &&
                thresholdId == that.thresholdId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensorId, thresholdId);
    }
}
