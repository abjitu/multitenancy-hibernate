package com.multitenancy.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.multitenancy.persistence.RequestBasedCurrentTenantIdentifierResolver;

public class RequestTimeInterceptor extends HandlerInterceptorAdapter {
    private final Logger log = LoggerFactory.getLogger(RequestTimeInterceptor.class);

    private final static String REQUEST_START_TIME = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.trace("inside preHandle");
        addRequestStartTime(request);
        RequestBasedCurrentTenantIdentifierResolver.tenantThreadLocal.set(request.getParameter("tenant"));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

        long startTime = (Long) request.getAttribute(REQUEST_START_TIME);
        long endTime = System.currentTimeMillis();

        long executeTime = endTime - startTime;

        log.info("Request Served in {} milli seconds", executeTime);
    }

    private void addRequestStartTime(HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        request.setAttribute(REQUEST_START_TIME, startTime);

        log.debug("Request start time is {}", startTime);
    }

}
