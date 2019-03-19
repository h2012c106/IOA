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

    public boolean adminUpdate(Integer id,String newPwd) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "UPDATE UserModel " +
                "SET pwd = (:pwd) " +
                "WHERE id = (:id)";
        int updateRow = tmpSession.createQuery(qry)
                .setParameter("pwd", newPwd)
                .setParameter("id", id)
                .executeUpdate();
        transaction.commit();
        return updateRow > 0;
    }
}
