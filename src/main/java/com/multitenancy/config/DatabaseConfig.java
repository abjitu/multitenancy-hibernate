package com.multitenancy.config;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.multitenancy.persistence.MultiTenantConnectionProviderFactory;
import com.multitenancy.persistence.RequestBasedCurrentTenantIdentifierResolver;

@Configuration
@PropertySource("classpath:/db-config.properties")
@EnableTransactionManagement
public class DatabaseConfig {
    private @Autowired Environment env;

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);
        return transactionManager;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource());
        sessionFactoryBean.setPackagesToScan("com.multitenancy.entity");
        sessionFactoryBean.setHibernateProperties(hibernateProperties());
        sessionFactoryBean.setMultiTenantConnectionProvider(tenantConnectionProvider());
        sessionFactoryBean.setCurrentTenantIdentifierResolver(tenantIdentifierResolver());
        return sessionFactoryBean;
    }

    @Bean
    public MultiTenantConnectionProviderFactory tenantConnectionProvider() {
        ConcurrentMap<String, BasicDataSource> dataSourceMap = new ConcurrentHashMap<String, BasicDataSource>();
        dataSourceMap.put("default", dataSource());

        MultiTenantConnectionProviderFactory tenantConnectionProvider = new MultiTenantConnectionProviderFactory();
        tenantConnectionProvider.setDataSourceMap(dataSourceMap);

        return tenantConnectionProvider;
    }
    
//    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Bean
    public RequestBasedCurrentTenantIdentifierResolver tenantIdentifierResolver() {
        RequestBasedCurrentTenantIdentifierResolver tenantIdentifierResolver = new RequestBasedCurrentTenantIdentifierResolver();
        return tenantIdentifierResolver;
    }

    @Bean
    public BasicDataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("db.driver"));
        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));
        dataSource.setMaxIdle(10);
        dataSource.setMaxWaitMillis(1000);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDefaultAutoCommit(true);
        dataSource.setValidationQuery("SELECT 1+1");
        dataSource.setTestOnBorrow(true);

        return dataSource;
    }

    private Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.dialect", "com.multitenancy.persistence.dialect.JsonbPostgreSQLDialect");
        hibernateProperties.put("hibernate.query.substitutions", "true 'Y', false 'N'");
        hibernateProperties.put("hibernate.show_sql", "false");
        hibernateProperties.put("hibernate.format_sql", "true");
        return hibernateProperties;
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        HibernateExceptionTranslator hibernateExceptionTranslator = new HibernateExceptionTranslator();
        return hibernateExceptionTranslator;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
