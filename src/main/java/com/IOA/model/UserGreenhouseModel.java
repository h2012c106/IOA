package com.IOA.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_greenhouse", schema = "ioa", catalog = "")
@IdClass(UserGreenhouseModelPK.class)
public class UserGreenhouseModel {
    private int userId;
    private int greenhouseId;

    public UserGreenhouseModel() {
    }

    public UserGreenhouseModel(int userId, int greenhouseId) {
        this.userId = userId;
        this.greenhouseId = greenhouseId;
    }

    @Id
    @Column(name = "user_id")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Id
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
        UserGreenhouseModel that = (UserGreenhouseModel) o;
        return userId == that.userId &&
                greenhouseId == that.greenhouseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, greenhouseId);
    }
}
