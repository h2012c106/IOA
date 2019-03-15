package com.IOA.dao;

import com.IOA.model.ResultModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

@SuppressWarnings("unchecked")
@Component
public class ResultDAO extends BasicDAO<ResultModel>{

    public List<ResultModel> searchLatestResult(Integer sensorId,Integer greenhouseId) {
        Session tmpSession = this.getTmpSession();
        String qry = "FROM ResultModel " +
                "WHERE sensorId = (:sensorId) AND greenhouseId = (:greenhouseId) "+
                "ORDER BY time DESC";
        return tmpSession.createQuery(qry)
                .setParameter("sensorId", sensorId)
                .setParameter("greenhouseId", greenhouseId)
                .setMaxResults(1)
                .list();
    }
}
