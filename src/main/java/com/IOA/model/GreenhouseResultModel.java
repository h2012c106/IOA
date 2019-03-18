package com.IOA.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "greenhouse_result", schema = "ioa", catalog = "")
@IdClass(GreenhouseResultModelPK.class)
public class GreenhouseResultModel {
    private int greenhouseId;
    private int resultId;

    public GreenhouseResultModel() {
    }

    public GreenhouseResultModel(int greenhouseId, int resultId) {
        this.greenhouseId = greenhouseId;
        this.resultId = resultId;
    }

    @Id
    @Column(name = "greenhouse_id")
    public int getGreenhouseId() {
        return greenhouseId;
    }

    public void setGreenhouseId(int greenhouseId) {
        this.greenhouseId = greenhouseId;
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
        GreenhouseResultModel that = (GreenhouseResultModel) o;
        return greenhouseId == that.greenhouseId &&
                resultId == that.resultId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(greenhouseId, resultId);
    }
}
