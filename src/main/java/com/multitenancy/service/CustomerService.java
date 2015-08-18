package com.multitenancy.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multitenancy.api.Response;
import com.multitenancy.api.Response.ResultType;
import com.multitenancy.dao.CustomerDao;
import com.multitenancy.entity.CustomerEntity;

@Service
public class CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private @Autowired CustomerDao customerDao;

    @Transactional
    public Response create(CustomerEntity entity) {
        CustomerEntity customer = readByEmail(entity.getEmail());
        Response result = null;
        try {
            if (null != customer) {
                result = Response.get(ResultType.FAILURE, "Customer Already Exists");
            } else {
                customerDao.save(entity);
                result = Response.get(ResultType.SUCCESS);
            }
        } catch (Exception e) {
            log.error("Exception : {}", e.getMessage(), e);
            result = Response.get(ResultType.FAILURE, e.getMessage());
        }
        return result;
    }

    @Transactional
    public Response update(CustomerEntity entity) {
        CustomerEntity customerEntity1 = customerDao.findByJsonb211();
        CustomerEntity customer = readByEmail(entity.getEmail());
        Response result = null;
        try {
            if (null != customer) {
                customer.setFirstName(entity.getFirstName());
                customer.setLastName(entity.getLastName());
                customer.setEmail(entity.getEmail());
                customer.setPattern(entity.getPattern());
                customerDao.update(customer);
                result = Response.get(ResultType.SUCCESS);
            }
        } catch (Exception e) {
            log.error("Exception : {}", e.getMessage(), e);
            result = Response.get(ResultType.FAILURE, e.getMessage());
        }
        return result;
    }

    @Transactional(readOnly = true)
    private CustomerEntity readByEmail1(String email) {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("email", email);
        return customerDao.findByNamedQuery("findByEmail", queryParams).get(0);
    }

    @Transactional(readOnly = true)
    private CustomerEntity readByJsonB() {
        return customerDao.findByJsonb2();
    }

    @Transactional(readOnly = true)
    private CustomerEntity readByEmail(String email) {
        return customerDao.findUniqueByProperty(CustomerDao.EMAIL, email);
    }

    @Transactional(readOnly = true)
    public List<CustomerEntity> get() {
        return customerDao.getAll();
    }

    @Transactional(readOnly = true)
    public CustomerEntity get(Long id) {
        return customerDao.get(id);
    }

    @Transactional
    public Response delete(Long id) {
        Response result = null;
        try {
            customerDao.remove(id);
            result = Response.get(ResultType.SUCCESS);
        } catch (Exception e) {
            log.error("Exception : {}", e.getMessage(), e);
            result = Response.get(ResultType.FAILURE, e.getMessage());
        }
        return result;
    }
}
