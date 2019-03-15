package com.IOA.dao;

import com.IOA.model.SensorModel;
import com.IOA.model.UserModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SensorDAO extends BasicDAO<SensorModel> {

    // 应对解绑的场合
    public void updateThreshold(List<Integer> thresholdIdArr) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "UPDATE SensorModel " +
                "SET thresholdId = -1 " +
                "WHERE thresholdId IN (:idArr)";
        tmpSession.createQuery(qry)
                .setParameterList("idArr", thresholdIdArr)
                .executeUpdate();
        transaction.commit();
    }
    public void updateThreshold(String clusterId) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "UPDATE SensorModel " +
                "SET thresholdId = -1 " +
                "WHERE clusterId = (:id)";
        tmpSession.createQuery(qry)
                .setParameter("id", clusterId)
                .executeUpdate();
        transaction.commit();
    }

    // 应对修改阈值的场合
    public boolean updateThreshold(Integer sensorId,Integer thresholdId) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "UPDATE SensorModel " +
                "SET thresholdId = (:thresholdId) " +
                "WHERE id = (:sensorId)";
        int updateRow = tmpSession.createQuery(qry)
                .setParameter("thresholdId", thresholdId)
                .setParameter("sensorId", sensorId)
                .executeUpdate();
        transaction.commit();
        return updateRow > 0;
    }
}
