package com.IOA.model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class SelectModelPK implements Serializable {
    private int sensorId;
    private int thresholdId;

    @Column(name = "sensor_id")
    @Id
    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    @Column(name = "threshold_id")
    @Id
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
        SelectModelPK that = (SelectModelPK) o;
        return sensorId == that.sensorId &&
                thresholdId == that.thresholdId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensorId, thresholdId);
    }
}
