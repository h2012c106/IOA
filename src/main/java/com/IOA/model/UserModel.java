package com.IOA.model;

import javax.validation.constraints.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "user", schema = "ioa", catalog = "")
public class UserModel {
    private int id;
    @NotNull
    private String name;
    @NotNull
    private String pwd;
    private String userType;

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
    @Column(name = "pwd")
    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Basic
    @Column(name = "user_type")
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserModel userModel = (UserModel) o;

        if (id != userModel.id) return false;
        if (name != null ? !name.equals(userModel.name) : userModel.name != null) return false;
        if (pwd != null ? !pwd.equals(userModel.pwd) : userModel.pwd != null) return false;
        if (userType != null ? !userType.equals(userModel.userType) : userModel.userType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (pwd != null ? pwd.hashCode() : 0);
        result = 31 * result + (userType != null ? userType.hashCode() : 0);
        return result;
    }
}
