package com.IOA.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "threshold", schema = "ioa", catalog = "")
public class ThresholdModel {
    private int id;

    @NotNull
    private String name;

    @NotNull
    private BigDecimal minimum;

    @NotNull
    private BigDecimal maximum;

    public ThresholdModel() {
    }

    public ThresholdModel(@NotNull String name, @NotNull BigDecimal minimum, @NotNull BigDecimal maximum) {
        this.name = name;
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
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        ThresholdModel that = (ThresholdModel) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(minimum, that.minimum) &&
                Objects.equals(maximum, that.maximum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, minimum, maximum);
    }
}
