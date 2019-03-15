package com.IOA.service;

import com.IOA.dao.ResultDAO;
import com.IOA.dao.SensorDAO;
import com.IOA.dao.SensorGreenhouseDAO;
import com.IOA.dao.ThresholdDAO;
import com.IOA.model.ResultModel;
import com.IOA.model.SensorGreenhouseModel;
import com.IOA.model.SensorModel;
import com.IOA.model.ThresholdModel;
import com.IOA.util.MyErrorType;
import com.IOA.util.MyPipe;
import com.IOA.vo.NormalMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SensorService {

    @Autowired
    SensorDAO SDAO;

    @Autowired
    ThresholdDAO TDAO;

    @Autowired
    ResultDAO RDAO;

    @Autowired
    SensorGreenhouseDAO SGDAO;

    @Autowired
    MyPipe Pipe;

    public NormalMessage registerThreshold(String token, ThresholdModel threshold) {
        Integer sensorId = threshold.getSensorId();
        String name = threshold.getName();

        // 看看该传感器下有没有同名阈值
        List<ThresholdModel> sameNameThreshold
                = TDAO.searchBySomeId(sensorId, "sensorId", name, "name");
        if (sameNameThreshold.size() != 0) {
            return new NormalMessage(false, MyErrorType.ThresholdNameDuplicate, null);
        }

        // 把传感器所属传感器群的大棚找出来
        List<SensorModel> singleSensor = SDAO.searchBySomeId(sensorId, "sensorId");
        if (singleSensor.size() == 0) {
            return new NormalMessage(false, MyErrorType.SensorUnexist, null);
        }

        String clusterId = singleSensor.get(0).getClusterId();
        List<SensorGreenhouseModel> singleCluster = SGDAO.searchBySomeId(clusterId, "clusterId");
        if (singleCluster.size() == 0) {
            return new NormalMessage(false, MyErrorType.ClusterUnexist, null);
        }

        int greenhouseId = singleCluster.get(0).getGreenhouseId();
        threshold.setGreenhouseId(greenhouseId);
        TDAO.save(threshold);
        return new NormalMessage(true, null, null);
    }

    public NormalMessage getInfo(String token, Integer sensorId) {
        List<SensorModel> singleSensor = SDAO.searchBySomeId(sensorId, "id");
        if (singleSensor.size() == 0) {
            return new NormalMessage(false, MyErrorType.SensorUnexist, null);
        } else {
            return new NormalMessage(true, null, singleSensor.get(0));
        }
    }

    public NormalMessage getLatestVal(String token, Integer sensorId) {
        List<SensorModel> singleSensor = SDAO.searchBySomeId(sensorId, "id");
        if (singleSensor.size() == 0) {
            return new NormalMessage(false, MyErrorType.SensorUnexist, null);
        }

        // 把传感器所属传感器群找出来
        String clusterId = singleSensor.get(0).getClusterId();
        List<SensorGreenhouseModel> singleCluster = SGDAO.searchBySomeId(clusterId, "clusterId");
        if (singleCluster.size() == 0) {
            return new NormalMessage(false, MyErrorType.ClusterUnexist, null);
        }
        Integer greenhouseId = singleCluster.get(0).getGreenhouseId();

        Timestamp timestamp = Pipe.getRefreshTime(clusterId);
        Map<Integer, Map<String, BigDecimal>> sensorCache
                = Pipe.getSensor2Server(clusterId);
        BigDecimal value = null;
        BigDecimal minimum = null;
        BigDecimal maximum = null;
        if (sensorCache == null || timestamp == null) {
            timestamp = null;
            List<ResultModel> singleResult = RDAO.searchLatestResult(sensorId, greenhouseId);
            if (singleResult.size() != 0) {
                timestamp = singleResult.get(0).getTime();
                value = singleResult.get(0).getValue();
                minimum = singleResult.get(0).getMinimum();
                maximum = singleResult.get(0).getMaximum();
            }
        } else {
            value = sensorCache.get(sensorId) == null
                    ? null : sensorCache.get(sensorId).get("value");
            minimum = sensorCache.get(sensorId) == null
                    ? null : sensorCache.get(sensorId).get("minimum");
            maximum = sensorCache.get(sensorId) == null
                    ? null : sensorCache.get(sensorId).get("maximum");
        }

        Map<String, Object> message = new HashMap<>();
        message.put("value", value);
        message.put("minimum", minimum);
        message.put("maximum", maximum);
        message.put("time", timestamp);
        return new NormalMessage(true, null, message);
    }

    public NormalMessage getHistoryVal(String token, Integer sensorId) {
        List<SensorModel> singleSensor = SDAO.searchBySomeId(sensorId, "id");
        if (singleSensor.size() == 0) {
            return new NormalMessage(false, MyErrorType.SensorUnexist, null);
        }

        // 把传感器所属传感器群的大棚找出来，用于过滤传感器读数
        String clusterId = singleSensor.get(0).getClusterId();
        List<SensorGreenhouseModel> singleCluster = SGDAO.searchBySomeId(clusterId, "clusterId");
        if (singleCluster.size() == 0) {
            return new NormalMessage(false, MyErrorType.ClusterUnexist, null);
        }
        int greenhouseId = singleCluster.get(0).getGreenhouseId();

        List<ResultModel> resultList
                = RDAO.searchBySomeId(sensorId, "sensorId", greenhouseId, "greenhouseId");

        return new NormalMessage(true, null, resultList);
    }

    public NormalMessage getThresholdList(String token, Integer sensorId) {
        List<ThresholdModel> thresholdArr = TDAO.searchBySomeId(sensorId, "sensorId");
        return new NormalMessage(true, null, thresholdArr);
    }

    public NormalMessage selectThreshold(String token, Integer sensorId, Integer thresholdId) {
        List<ThresholdModel> singleThreshold
                = TDAO.searchBySomeId(sensorId, "sensorId", thresholdId, "id");
        if (singleThreshold.size() == 0) {
            return new NormalMessage(false, MyErrorType.ThresholdUnexist, null);
        }

        return SDAO.updateThreshold(sensorId, thresholdId)
                ? new NormalMessage(true, null, null)
                : new NormalMessage(false, MyErrorType.UpdateError, null);
    }

    public NormalMessage unbindThreshold(String token, Integer sensorId, Integer thresholdId) {
        TDAO.deleteBySomeId(sensorId, "sensorId", thresholdId, "id");
        SDAO.updateThreshold(sensorId, -1);
        return new NormalMessage(true, null, null);
    }
}
