package com.IOA.model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class UserGreenhouseModelPK implements Serializable {
    private int userId;
    private int greenhouseId;

    @Column(name = "user_id")
    @Id
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Column(name = "greenhouse_id")
    @Id
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
        UserGreenhouseModelPK that = (UserGreenhouseModelPK) o;
        return userId == that.userId &&
                greenhouseId == that.greenhouseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, greenhouseId);
    }
}
