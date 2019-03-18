package com.IOA.dao;

import com.IOA.model.GreenhouseClusterModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

@Component
public class GreenhouseClusterDAO extends BasicDAO<GreenhouseClusterModel> {
    public void updateName(String clusterId, String newName) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "UPDATE GreenhouseClusterModel " +
                "SET name = (:name) " +
                "WHERE clusterId = (:clusterId)";
        tmpSession.createQuery(qry)
                .setParameter("name", newName)
                .setParameter("clusterId", clusterId)
                .executeUpdate();
        transaction.commit();
    }
}
