package com.multitenancy.persistence;

import java.util.concurrent.ConcurrentMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.hibernate.service.spi.Stoppable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@PropertySource("classpath:/db-config.properties")
public class MultiTenantConnectionProviderFactory extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl
        implements Stoppable {

    private @Autowired Environment env;

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected static final Logger logger = LoggerFactory.getLogger(MultiTenantConnectionProviderFactory.class);

    private ConcurrentMap<String, BasicDataSource> dataSourceMap;

    @Override
    protected DataSource selectAnyDataSource() {
        return (BasicDataSource) dataSourceMap.values().toArray()[0];
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        BasicDataSource dataSource = dataSourceMap.get(tenantIdentifier);
        if (null == dataSource) {
            dataSource(tenantIdentifier);
        }
        return dataSourceMap.get(tenantIdentifier);
    }

    private synchronized void dataSource(String tenant) {
        BasicDataSource dataSource = dataSourceMap.get(tenant);
        if (null == dataSource) {
            logger.info("Creating datasource for tenant : {}", tenant);
            dataSource = new BasicDataSource();
            dataSource.setDriverClassName(env.getProperty("db.driver"));
            dataSource.setUrl(env.getProperty("db.url") + tenant);
            dataSource.setUsername(env.getProperty("db.username"));
            dataSource.setPassword(env.getProperty("db.password"));
            dataSource.setMaxIdle(10);
            dataSource.setMaxWaitMillis(1000);
            dataSource.setPoolPreparedStatements(true);
            dataSource.setDefaultAutoCommit(true);
            dataSource.setValidationQuery("SELECT 1+1");
            dataSource.setTestOnBorrow(true);
            dataSourceMap.put(tenant, dataSource);
        }
    }

    public ConcurrentMap<String, BasicDataSource> getDataSourceMap() {
        return dataSourceMap;
    }

    public void setDataSourceMap(ConcurrentMap<String, BasicDataSource> dataSourceMap) {
        this.dataSourceMap = dataSourceMap;
    }

    @Override
    public void stop() {
        if (dataSourceMap != null) {
            dataSourceMap.clear();
            dataSourceMap = null;
        }
    }
}
