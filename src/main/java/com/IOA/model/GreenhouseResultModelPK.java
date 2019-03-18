package com.IOA.model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class GreenhouseResultModelPK implements Serializable {
    private int greenhouseId;
    private int resultId;

    @Column(name = "greenhouse_id")
    @Id
    public int getGreenhouseId() {
        return greenhouseId;
    }

    public void setGreenhouseId(int greenhouseId) {
        this.greenhouseId = greenhouseId;
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
        GreenhouseResultModelPK that = (GreenhouseResultModelPK) o;
        return greenhouseId == that.greenhouseId &&
                resultId == that.resultId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(greenhouseId, resultId);
    }
}
