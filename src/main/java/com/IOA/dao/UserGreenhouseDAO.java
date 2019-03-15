package com.IOA.dao;

import com.IOA.model.UserGreenhouseModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserGreenhouseDAO extends BasicDAO<UserGreenhouseModel> {

    public boolean doesGreenhouseBelongToUser(int userId, int greenhouseId) {
        // 这个用户名下的大棚列表
        List<UserGreenhouseModel> UGList = this.searchBySomeId(userId, "userId");

        // 若在此用户的大棚列表中找不到他要访问的，那么禁止访问
        boolean isHis = false;
        for (UserGreenhouseModel tmpUG : UGList) {
            if (greenhouseId == tmpUG.getGreenhouseId()) {
                isHis = true;
                break;
            }
        }
        return isHis;
    }

}
