package com.IOA.model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class SensorResultModelPK implements Serializable {
    private int sensorId;
    private int resultId;

    @Column(name = "sensor_id")
    @Id
    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    @Column(name = "result_id")
    @Id
    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorResultModelPK that = (SensorResultModelPK) o;
        return sensorId == that.sensorId &&
                resultId == that.resultId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensorId, resultId);
    }
}
