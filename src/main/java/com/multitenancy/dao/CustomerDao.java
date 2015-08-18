package com.multitenancy.dao;

import java.util.List;
import java.util.Properties;

import org.hibernate.internal.TypeLocatorImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;
import org.springframework.stereotype.Repository;

import com.multitenancy.entity.CustomerEntity;
import com.multitenancy.persistence.usertype.JsonbUserType;

@Repository
public class CustomerDao extends BaseDao<CustomerEntity, Long> {
    public static final String EMAIL = "email";

    public CustomerDao() {
        super(CustomerEntity.class);
    }

    public CustomerEntity findByJsonb1() {
        CustomerEntity customerEntity = null;

        List<CustomerEntity> result = findByNamedQuery("findByJsonB1");
        if (null != result && result.size() > 0) {
            customerEntity = result.get(0);
        }
        return customerEntity;
    }

    @SuppressWarnings("unchecked")
    public CustomerEntity findByJsonb21() {
        CustomerEntity customerEntity = null;
        Properties params = new Properties();
        params.put(JsonbUserType.CLASS, "com.multitenancy.entity.Patterns");
        Type jsonUserType = new TypeLocatorImpl(new TypeResolver()).custom(JsonbUserType.class, params);
        List<CustomerEntity> result = getSession().createSQLQuery("select pattern from customer where pattern @> '{}'")
                .addScalar("pattern", jsonUserType)
                .setResultTransformer(Transformers.aliasToBean(CustomerEntity.class)).list();
        if (null != result && result.size() > 0) {
            customerEntity = result.get(0);
        }
        return customerEntity;
    }

    @SuppressWarnings("unchecked")
    public CustomerEntity findByJsonb211() {
        CustomerEntity customerEntity = null;
        Properties params = new Properties();
        params.put(JsonbUserType.CLASS, "java.util.ArrayList");
        Type jsonUserType = new TypeLocatorImpl(new TypeResolver()).custom(JsonbUserType.class, params);
        List<CustomerEntity> result = getSession()
                .createSQLQuery("select pattern->'patternList' as test from customer;").addScalar("test", jsonUserType)
                .list();
        if (null != result && result.size() > 0) {
            customerEntity = result.get(0);
        }
        return customerEntity;
    }

    public CustomerEntity findByJsonb22() {
        CustomerEntity customerEntity = null;
        List<CustomerEntity> result = getByNamedQuery("findByJsonB2");
        if (null != result && result.size() > 0) {
            customerEntity = result.get(0);
        }
        return customerEntity;
    }

    public CustomerEntity findByJsonb2() {
        CustomerEntity customerEntity = null;
        List<CustomerEntity> result = getByNamedQuery("findByJsonB3");
        if (null != result && result.size() > 0) {
            customerEntity = result.get(0);
        }
        return customerEntity;
    }
}
