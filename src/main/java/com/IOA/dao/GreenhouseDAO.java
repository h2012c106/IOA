package com.IOA.dao;

import com.IOA.model.ThresholdModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.IOA.model.GreenhouseModel;

import java.util.List;

@Component
public class GreenhouseDAO extends BasicDAO<GreenhouseModel> {

    public boolean update(GreenhouseModel greenhouseModel) {
        Session tmpSession = this.getTmpSession();
        Transaction transaction = tmpSession.beginTransaction();
        String qry = "UPDATE GreenhouseModel " +
                "SET name = (:name), crop = (:crop), status = (:status), location = (:location) " +
                "WHERE id = (:id)";
        try {
            int updateRow = tmpSession.createQuery(qry)
                    .setParameter("name", greenhouseModel.getName())
                    .setParameter("crop", greenhouseModel.getCrop())
                    .setParameter("status", greenhouseModel.getStatus())
                    .setParameter("location", greenhouseModel.getLocation())
                    .setParameter("id", greenhouseModel.getId())
                    .executeUpdate();
            transaction.commit();
            return updateRow > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
