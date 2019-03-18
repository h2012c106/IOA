package com.IOA.dao;

import com.IOA.model.ClusterModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClusterDAO extends BasicDAO<ClusterModel> {
    public boolean updateStatus(String clusterId, String status) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "UPDATE ClusterModel " +
                "SET status = (:status) " +
                "WHERE id = (:clusterId) AND status <> 'error'";
        try {
            tmpSession.createQuery(qry)
                    .setParameter("status", status)
                    .setParameter("id", clusterId)
                    .executeUpdate();
            transaction.commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateStatus(List<Object> clusterIdArr, String status) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "UPDATE ClusterModel " +
                "SET status = (:status) " +
                "WHERE id IN (:clusterId) AND status <> 'error'";
        try {
            tmpSession.createQuery(qry)
                    .setParameter("status", status)
                    .setParameterList("id", clusterIdArr)
                    .executeUpdate();
            transaction.commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
