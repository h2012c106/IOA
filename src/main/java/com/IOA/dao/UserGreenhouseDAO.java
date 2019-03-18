package com.IOA.dao;

import com.IOA.model.UserGreenhouseModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserGreenhouseDAO extends BasicDAO<UserGreenhouseModel> {

    public boolean doesGreenhouseBelongToUser(int userId, int greenhouseId) {
        List<UserGreenhouseModel> UGList
                = this.searchBySomeId(userId, "userId", greenhouseId, "greenhouseId");

        return UGList.size() > 0;
    }

}
