package com.IOA.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;

public class LoggerInterceptor extends HandlerInterceptorAdapter {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("beginTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        int status = ex == null ? response.getStatus() : 500;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String beginTimeStr = dateFormat.format(request.getAttribute("beginTime"));
        long interval = System.currentTimeMillis() - (Long) request.getAttribute("beginTime");

        String log = String.format("[%s] %s %s -> %d (%dms)", beginTimeStr, requestURI, method, status, interval);
        logger.info(log);
    }

}
