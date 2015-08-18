package com.multitenancy.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multitenancy.api.Response.ResultType;

@RestController
public class WelcomeApi {

    private static Logger logger = LoggerFactory.getLogger(WelcomeApi.class);

    @RequestMapping("/")
    public Response hello() {
        logger.debug("Entering hello");
        return Response.get(ResultType.SUCCESS, "Hello!! Welcome to Spring Hibernate JPA Multi Tenancy Demo");
    }

}
