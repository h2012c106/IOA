package com.IOA.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.IOA.model.SensorGreenhouseModel;

@Component
public class SensorGreenhouseDAO extends BasicDAO<SensorGreenhouseModel> {
    public void updateGreenhouseState(Integer greenhouseId, String newState) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "UPDATE SensorGreenhouseModel " +
                "SET status = (:state) " +
                "WHERE greenhouseId = (:greenhouseId) AND state <> 'error'";
        tmpSession.createQuery(qry)
                .setParameter("state", newState)
                .setParameter("greenhouseId", greenhouseId)
                .executeUpdate();
        transaction.commit();
    }

    public void updateClusterInfo(String clusterId, String newState, String newName) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "UPDATE SensorGreenhouseModel " +
                "SET status = (:state), name = (:name) " +
                "WHERE clusterId = (:clusterId)";
        tmpSession.createQuery(qry)
                .setParameter("state", newState)
                .setParameter("name", newName)
                .setParameter("clusterId", clusterId)
                .executeUpdate();
        transaction.commit();
    }
}
