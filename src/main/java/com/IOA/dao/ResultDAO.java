package com.IOA.dao;

import com.IOA.model.ResultModel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
@Component
public class ResultDAO extends BasicDAO<ResultModel> {

    public ResultModel searchLatestResult(Integer sensorId, Integer greenhouseId) {
        Session tmpSession = this.getTmpSession();
        String qry = "SELECT r.time, r.value, r.minimum, r.maximum " +
                "FROM ResultModel r INNER JOIN GreenhouseResultModel gr ON r.id = gr.resultId INNER JOIN SensorResultModel sr ON r.id = sr.resultId " +
                "WHERE gr.greenhouseId = (:greenhouseId) AND sr.sensorId = (:sensorId)  " +
                "ORDER BY r.time DESC";
        List<Object[]> res = tmpSession.createQuery(qry)
                .setParameter("sensorId", sensorId)
                .setParameter("greenhouseId", greenhouseId)
                .setMaxResults(1)
                .list();
        if (res.size() == 0) {
            return null;
        } else {
            Timestamp time = (Timestamp) res.get(0)[0];
            BigDecimal value = (BigDecimal) res.get(0)[1];
            BigDecimal minimum = (BigDecimal) res.get(0)[2];
            BigDecimal maximum = (BigDecimal) res.get(0)[3];
            return new ResultModel(value, time, minimum, maximum);
        }
    }

    public List<ResultModel> searchHistoryResult(Integer sensorId, Integer greenhouseId) {
        Session tmpSession = this.getTmpSession();
        String qry = "SELECT r.time, r.value, r.minimum, r.maximum " +
                "FROM ResultModel r INNER JOIN GreenhouseResultModel gr ON r.id = gr.resultId INNER JOIN SensorResultModel sr ON r.id = sr.resultId " +
                "WHERE gr.greenhouseId = (:greenhouseId) AND sr.sensorId = (:sensorId)  " +
                "ORDER BY r.time DESC";
        List<Object[]> tmpRes = tmpSession.createQuery(qry)
                .setParameter("sensorId", sensorId)
                .setParameter("greenhouseId", greenhouseId)
                .list();
        List<ResultModel> res = new ArrayList<>();
        for (Object[] tmpArr : tmpRes) {
            Timestamp time = (Timestamp) tmpArr[0];
            BigDecimal value = (BigDecimal) tmpArr[1];
            BigDecimal minimum = (BigDecimal) tmpArr[2];
            BigDecimal maximum = (BigDecimal) tmpArr[3];
            res.add(new ResultModel(value, time, minimum, maximum));
        }
        return res;
    }
}
