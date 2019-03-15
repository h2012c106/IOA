package com.IOA.dao;

import com.IOA.model.DeviceModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeviceDAO extends BasicDAO<DeviceModel> {
    public void updateStatus(Integer id, String status) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "UPDATE DeviceModel " +
                "SET status = (:status) " +
                "WHERE id = (:id)";
        tmpSession.createQuery(qry)
                .setParameter("status", status)
                .setParameter("id", id)
                .executeUpdate();
        transaction.commit();
    }

    public void closeAll(String clusterId){
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "UPDATE DeviceModel " +
                "SET status = 'close' " +
                "WHERE clusterId = (:clusterId) AND status = 'on'";
        tmpSession.createQuery(qry)
                .setParameter("clusterId", clusterId)
                .executeUpdate();
        transaction.commit();
    }

    public void closeAll(List<String> clusterIdArr){
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "UPDATE DeviceModel " +
                "SET status = 'close' " +
                "WHERE clusterId IN (:clusterIdArr) AND status = 'on'";
        tmpSession.createQuery(qry)
                .setParameterList("clusterIdArr", clusterIdArr)
                .executeUpdate();
        transaction.commit();
    }
}
