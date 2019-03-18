package com.IOA.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "result", schema = "ioa", catalog = "")
public class ResultModel {
    private int id;
    private BigDecimal value;
    private Timestamp time;
    private BigDecimal minimum;
    private BigDecimal maximum;

    public ResultModel() {
    }

    public ResultModel(BigDecimal value, Timestamp time, BigDecimal minimum, BigDecimal maximum) {
        this.value = value;
        this.time = time;
        this.minimum = minimum;
        this.maximum = maximum;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResultModel that = (ResultModel) o;
        return id == that.id &&
                Objects.equals(value, that.value) &&
                Objects.equals(time, that.time) &&
                Objects.equals(minimum, that.minimum) &&
                Objects.equals(maximum, that.maximum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, time, minimum, maximum);
    }
}
