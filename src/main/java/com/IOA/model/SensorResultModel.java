package com.IOA.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "sensor_result", schema = "ioa", catalog = "")
@IdClass(SensorResultModelPK.class)
public class SensorResultModel {
    private int sensorId;
    private int resultId;

    public SensorResultModel() {
    }

    public SensorResultModel(int sensorId, int resultId) {
        this.sensorId = sensorId;
        this.resultId = resultId;
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
    @Column(name = "result_id")
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
        SensorResultModel that = (SensorResultModel) o;
        return sensorId == that.sensorId &&
                resultId == that.resultId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensorId, resultId);
    }
}
