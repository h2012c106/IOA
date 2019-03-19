package com.IOA.interceptor;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.IOA.util.TokenManager;
import com.IOA.model.UserGreenhouseModel;
import com.IOA.dao.UserGreenhouseDAO;
import com.IOA.dao.UserDAO;

@Component
public class AuthorityInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private UserGreenhouseDAO UGDAO;

    @Autowired
    private UserDAO UDAO;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "用户认证失败: 缺少认证, 请先登录");
            return false;
        }

        final Map<String, Object> userInfo = TokenManager.parseToken(token);

        // 首先判断用户的登录状态是否合法
        if ((Integer) userInfo.get("error") == TokenManager.ExpiredJwtException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "用户认证失败: 认证过期, 请再次登陆");
            return false;
        }
        if ((Integer) userInfo.get("error") == TokenManager.SignatureException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "用户认证失败: 签名错误");
            return false;
        }

        // 判断管理员/用户权限
        String path = request.getContextPath();
        String uri = request.getRequestURI();
        boolean isNotAdmin = !userInfo.get("userType").equals("admin") &&
                uri.startsWith(path + "/Admin");
        boolean isNotFarmer = !userInfo.get("userType").equals("farmer") &&
                uri.startsWith(path + "/User");
        if (isNotAdmin || isNotFarmer) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "用户越权: 无权访问");
            return false;
        }

        // 可能需要添加：判断token中的用户是否存在+用户权限是否正确，
        // 不过如果能确保注销与更改权限都会向前端更新token也无所谓
        if (UDAO.searchBySomeId(userInfo.get("id"), "id").size() == 0) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "用户越权: 用户不存在");
            return false;
        }

        // 需要读两次request中的body流，不现实
//        // 管理员能看所有东西的信息
//        if (userInfo.get("userType").equals("farmer")) {
//            System.out.println("验一下！");
//
//            // 如果用户请求中包含大棚字段，那么判断大棚是否属于用户
//            Object greenhouseId = getBodyString(request).get("greenhouseId");
//            if (greenhouseId != null) {
//                System.out.println("拿到了！" + greenhouseId);
//
//                // 这个用户名下的大棚列表
//                List<UserGreenhouseModel> UGList = UGDAO.searchBySomeId(userInfo.get("id"), "userId");
//
//                // 这个用户企图访问/操作的大棚
//                Integer tmpGreenhouseId = (Integer) greenhouseId;
//
//                // 若在此用户的大棚列表中找不到他要访问的，那么禁止访问
//                boolean isHis = false;
//                for (UserGreenhouseModel tmpUG : UGList) {
//                    if (tmpGreenhouseId.equals(tmpUG.getGreenhouseId())) {
//                        isHis = true;
//                        break;
//                    }
//                }
//                if (!isHis) {
//                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
//                            "用户越权: 无法管理他人大棚");
//                    return false;
//                }
//            }
//
////            // 如果用户请求中包含传感器字段，那么判断传感器是否属于用户
////            // 这样验证的话就会拒绝同大棚下其他人的访问，虽然现在每个大棚就一个人
////            if (request.getParameter("sensorId") != null) {
////                // 这个用户名下的大棚列表
////                List<UserSensorModel> USList = USDAO.searchBySomeId(new ArrayList<Object>() {{
////                    add(userInfo.get("id"));
////                }}, "userId");
////
////                // 这个用户企图访问/操作的传感器
////                String tmpSensorId = request.getParameter("clusterId");
////
////                // 若在此用户的传感器列表中找不到他要访问的，那么禁止访问
////                boolean isHis = false;
////                for (UserSensorModel tmpUS : USList) {
////                    if (tmpSensorId.equals(tmpUS.getClusterId())) {
////                        isHis = true;
////                        break;
////                    }
////                }
////                if (!isHis) {
////                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
////                            "用户越权: 无法管理他人传感器");
////                    return false;
////                }
////            }
//        }

        return true;
    }

//    private Map getBodyString(HttpServletRequest request) throws IOException {
//        BufferedReader br = request.getReader();
//        String inputLine;
//        StringBuilder str = new StringBuilder();
//        try {
//            while ((inputLine = br.readLine()) != null) {
//                str.append(inputLine);
//            }
//            br.close();
//        } catch (IOException e) {
//            System.out.println("IOException: " + e);
//        }
//        if (str.length() == 0) {
//            return new HashMap();
//        } else {
//            return JSONObject.fromObject(str.toString());
//        }
//    }
}
