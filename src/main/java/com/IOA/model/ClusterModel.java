package com.IOA.model;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@DynamicInsert
@Table(name = "cluster", schema = "ioa", catalog = "")
public class ClusterModel {
    @NotNull
    @Length(min = 17, max = 17)
    private String id;

    @NotNull
    @Length(min = 6, max = 6)
    private String pwd;

    private String status;

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "pwd")
    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Basic
    @Column(name = "status", columnDefinition = "enum('error','on','close') DEFAULT 'on'")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClusterModel that = (ClusterModel) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(pwd, that.pwd) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pwd, status);
    }
}
