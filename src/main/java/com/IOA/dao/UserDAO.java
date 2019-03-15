package com.IOA.dao;

import com.IOA.model.UserModel;
import org.springframework.stereotype.Component;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Component
public class UserDAO extends BasicDAO<UserModel> {

    public boolean userUpdate(UserModel userModel) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "UPDATE UserModel " +
                "SET name = (:name), pwd = (:pwd) " +
                "WHERE id = (:id)";
        int updateRow = tmpSession.createQuery(qry)
                .setParameter("name", userModel.getName())
                .setParameter("pwd", userModel.getPwd())
                .setParameter("id", userModel.getId())
                .executeUpdate();
        transaction.commit();
        return updateRow > 0;
    }

    public boolean adminUpdate(UserModel userModel) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "UPDATE UserModel " +
                "SET name = (:name), pwd = (:pwd), userType = (:userType) " +
                "WHERE id = (:id)";
        int updateRow = tmpSession.createQuery(qry)
                .setParameter("name", userModel.getName())
                .setParameter("pwd", userModel.getPwd())
                .setParameter("userType", userModel.getUserType())
                .setParameter("id", userModel.getId())
                .executeUpdate();
        transaction.commit();
        return updateRow > 0;
    }
}
