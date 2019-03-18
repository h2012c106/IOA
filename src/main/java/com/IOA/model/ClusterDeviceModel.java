package com.IOA.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cluster_device", schema = "ioa", catalog = "")
@IdClass(ClusterDeviceModelPK.class)
public class ClusterDeviceModel {
    private String clusterId;
    private int deviceId;
    private String nickname;

    @Id
    @Column(name = "cluster_id")
    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    @Id
    @Column(name = "device_id")
    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    @Basic
    @Column(name = "nickname")
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClusterDeviceModel that = (ClusterDeviceModel) o;
        return deviceId == that.deviceId &&
                Objects.equals(clusterId, that.clusterId) &&
                Objects.equals(nickname, that.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clusterId, deviceId, nickname);
    }
}
