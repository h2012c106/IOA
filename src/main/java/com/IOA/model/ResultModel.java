package com.IOA.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "result", schema = "ioa", catalog = "")
public class ResultModel {
    private int id;
    private int sensorId;
    private BigDecimal value;
    private Timestamp time;
    private BigDecimal minimum;
    private BigDecimal maximum;
    private int greenhouseId;

    public ResultModel() {
    }

    public ResultModel(int sensorId,
                       BigDecimal value,
                       Timestamp time,
                       BigDecimal minimum,
                       BigDecimal maximum,
                       int greenhouseId) {
        this.sensorId = sensorId;
        this.value = value;
        this.time = time;
        this.minimum = minimum;
        this.maximum = maximum;
        this.greenhouseId = greenhouseId;
    }

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
    @Column(name = "sensor_id")
    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    @Basic
    @Column(name = "value")
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Basic
    @Column(name = "time")
    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Basic
    @Column(name = "minimum")
    public BigDecimal getMinimum() {
        return minimum;
    }

    public void setMinimum(BigDecimal minimum) {
        this.minimum = minimum;
    }

    @Basic
    @Column(name = "maximum")
    public BigDecimal getMaximum() {
        return maximum;
    }

    public void setMaximum(BigDecimal maximum) {
        this.maximum = maximum;
    }

    @Basic
    @Column(name = "greenhouse_id")
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
        ResultModel that = (ResultModel) o;
        return id == that.id &&
                sensorId == that.sensorId &&
                greenhouseId == that.greenhouseId &&
                Objects.equals(value, that.value) &&
                Objects.equals(time, that.time) &&
                Objects.equals(minimum, that.minimum) &&
                Objects.equals(maximum, that.maximum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sensorId, value, time, minimum, maximum, greenhouseId);
    }
}
