package com.IOA.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "sensor", schema = "ioa", catalog = "")
public class SensorModel {
    private int id;
    private String type;
    private String unit;

    public SensorModel() {
    }

    public SensorModel(String type, String unit) {
        this.type = type;
        this.unit = unit;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorModel that = (SensorModel) o;
        return id == that.id &&
                Objects.equals(type, that.type) &&
                Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, unit);
    }
}
