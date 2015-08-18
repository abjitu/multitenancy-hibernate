package com.multitenancy.persistence;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestBasedCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    private static final Logger logger = LoggerFactory.getLogger(RequestBasedCurrentTenantIdentifierResolver.class);

    public static final ThreadLocal<String> tenantThreadLocal = new ThreadLocal<String>(); 
    
    @Override
    public String resolveCurrentTenantIdentifier() {

        String tenantId = tenantThreadLocal.get();

        logger.info("tenantId : {}", tenantId);

        return tenantId;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
