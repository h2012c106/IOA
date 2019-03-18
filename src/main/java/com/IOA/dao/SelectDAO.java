package com.IOA.dao;

import com.IOA.model.SelectModel;
import org.springframework.stereotype.Component;

@Component
public class SelectDAO extends BasicDAO<SelectModel> {
    public void select(Integer sensorId, Integer thresholdId) {
        // 如果已选过阈值，那么先取消选择
        if (this.searchBySomeId(sensorId, "sensorId").size() != 0) {
            this.deleteBySomeId(sensorId, "sensorId");
        }
        this.save(new SelectModel(sensorId, thresholdId));
    }
}
