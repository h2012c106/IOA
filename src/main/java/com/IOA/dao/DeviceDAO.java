package com.IOA.dao;

import com.IOA.model.DeviceModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeviceDAO extends BasicDAO<DeviceModel> {

    public void updateStatus(Integer deviceId, String newStatus) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "UPDATE DeviceModel " +
                "SET status = (:status) " +
                "WHERE id = (:id)";
        try {
            tmpSession.createQuery(qry)
                    .setParameter("status", newStatus)
                    .setParameter("id", deviceId)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception ignored) {
        }
    }

    public void closeAll(List<Object> deviceIdArr) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "UPDATE DeviceModel " +
                "SET status = 'close' " +
                "WHERE id IN (:id) AND status = 'on'";
        tmpSession.createQuery(qry)
                .setParameterList("id", deviceIdArr)
                .executeUpdate();
        transaction.commit();
    }
}
