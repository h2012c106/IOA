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
        if (clusterIdArr != null && clusterIdArr.size() != 0) {
            Session tmpSession = this.getTmpSession();
            Transaction transaction = tmpSession.beginTransaction();
            String qry = "UPDATE ClusterModel " +
                    "SET status = (:status) " +
                    "WHERE id IN (:clusterId) AND status <> 'error'";
            boolean res;
            try {
                tmpSession.createQuery(qry)
                        .setParameter("status", status)
                        .setParameterList("clusterId", clusterIdArr)
                        .executeUpdate();
                res = true;
            } catch (Exception e) {
                res = false;
                System.out.println(e);
            } finally {
                transaction.commit();
            }
            return res;
        }
        return true;
    }

    public boolean updatePwd(String id, String newPwd) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "UPDATE ClusterModel " +
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
