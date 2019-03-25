package com.IOA.service;

import com.IOA.dao.*;
import com.IOA.model.*;
import com.IOA.util.MyErrorType;
import com.IOA.util.MyPipe;
import com.IOA.dto.NormalMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SensorService {

    @Autowired
    GreenhouseClusterDAO GCDAO;

    @Autowired
    ClusterDAO CDAO;

    @Autowired
    ClusterSensorDAO CSDAO;

    @Autowired
    SensorDAO SDAO;

    @Autowired
    SelectDAO SeDAO;

    @Autowired
    SensorThresholdDAO STDAO;

    @Autowired
    ThresholdDAO TDAO;

    @Autowired
    ResultDAO RDAO;

    @Autowired
    MyPipe Pipe;

    public NormalMessage registerThreshold(String token, Integer sensorId, String name,
                                           BigDecimal minimum, BigDecimal maximum) {

        // 看看该传感器下有没有同名阈值
        List<SensorThresholdModel> thresholdOfSensorArr
                = STDAO.searchBySomeId(sensorId, "sensorId");
        List<Object> thresholdIdOfSensorArr = thresholdOfSensorArr.stream()
                .map(SensorThresholdModel::getThresholdId)
                .collect(Collectors.toList());
        if (TDAO.isNameDuplicate(thresholdIdOfSensorArr, name)) {
            return new NormalMessage(false, MyErrorType.ThresholdNameDuplicate, null);
        }

        Integer newId = TDAO.saveBackId(new ThresholdModel(name, minimum, maximum));
        STDAO.save(new SensorThresholdModel(sensorId, newId));
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
        List<ClusterSensorModel> singleCS = CSDAO.searchBySomeId(sensorId, "sensorId");
        if (singleCS.size() == 0) {
            return new NormalMessage(false, MyErrorType.SensorUnexist, null);
        }
        String clusterId = singleCS.get(0).getClusterId();

        // 开始计时
        long startTime = System.currentTimeMillis();
        String hasCache;

        Timestamp timestamp = Pipe.getRefreshTime(clusterId);
        Map<Integer, Map<String, BigDecimal>> sensorCache
                = Pipe.getSensor2Server(clusterId);
        BigDecimal value = null;
        BigDecimal minimum = null;
        BigDecimal maximum = null;
        if (sensorCache == null || timestamp == null) {
            hasCache = "无";

            timestamp = null;

            // 把传感器群所属的大棚找出来
            List<GreenhouseClusterModel> singleGC = GCDAO.searchBySomeId(clusterId, "clusterId");
            if (singleGC.size() == 0) {
                return new NormalMessage(false, MyErrorType.ClusterUnexist, null);
            }
            Integer greenhouseId = singleGC.get(0).getGreenhouseId();

            ResultModel singleResult = RDAO.searchLatestResult(sensorId, greenhouseId);
            if (singleResult != null) {
                timestamp = singleResult.getTime();
                value = singleResult.getValue();
                minimum = singleResult.getMinimum();
                maximum = singleResult.getMaximum();
            }
        } else {
            hasCache = "有";

            value = sensorCache.get(sensorId) == null
                    ? null : sensorCache.get(sensorId).get("value");
            minimum = sensorCache.get(sensorId) == null
                    ? null : sensorCache.get(sensorId).get("minimum");
            maximum = sensorCache.get(sensorId) == null
                    ? null : sensorCache.get(sensorId).get("maximum");
        }

        // 停止计时
        System.out.println(hasCache + "缓存时取出单个传感器数据串花了: " + (System.currentTimeMillis() - startTime) + "ms");

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

        // 把传感器所属传感器群找出来
        List<ClusterSensorModel> singleCS = CSDAO.searchBySomeId(sensorId, "sensorId");
        if (singleCS.size() == 0) {
            return new NormalMessage(false, MyErrorType.SensorUnexist, null);
        }
        String clusterId = singleCS.get(0).getClusterId();

        // 把传感器群所属的大棚找出来
        List<GreenhouseClusterModel> singleGC = GCDAO.searchBySomeId(clusterId, "clusterId");
        if (singleGC.size() == 0) {
            return new NormalMessage(false, MyErrorType.ClusterUnexist, null);
        }
        Integer greenhouseId = singleGC.get(0).getGreenhouseId();

        List<ResultModel> resultList
                = RDAO.searchHistoryResult(sensorId, greenhouseId);

        return new NormalMessage(true, null, resultList);
    }

    public NormalMessage getThresholdList(String token, Integer sensorId) {
        List<SensorThresholdModel> thresholdOfSensorArr
                = STDAO.searchBySomeId(sensorId, "sensorId");
        List<Object> thresholdIdOfSensorArr = thresholdOfSensorArr.stream()
                .map(SensorThresholdModel::getThresholdId)
                .collect(Collectors.toList());
        List<ThresholdModel> thresholdArr = TDAO.searchBySomeId(thresholdIdOfSensorArr, "id");
        return new NormalMessage(true, null, thresholdArr);
    }

    public NormalMessage selectThreshold(String token, Integer sensorId, Integer thresholdId) {
        List<SensorThresholdModel> thresholdOfSensorArr
                = STDAO.searchBySomeId(sensorId, "sensorId", thresholdId, "thresholdId");
        if (thresholdOfSensorArr.size() == 0) {
            return new NormalMessage(false, MyErrorType.ThresholdUnexist, null);
        }

        SeDAO.select(sensorId, thresholdId);
        return new NormalMessage(true, null, null);
    }

    public NormalMessage unbindThreshold(String token, Integer sensorId, Integer thresholdId) {
        SeDAO.deleteBySomeId(thresholdId, "thresholdId");
        STDAO.deleteBySomeId(thresholdId, "thresholdId");
        TDAO.deleteBySomeId(thresholdId, "id");
        return new NormalMessage(true, null, null);
    }
}
