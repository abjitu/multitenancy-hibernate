package com.multitenancy.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.multitenancy.entity.CustomerEntity;
import com.multitenancy.service.CustomerService;

@RestController
@RequestMapping(value = "/customer", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class CustomerApi {
    private static final Logger log = LoggerFactory.getLogger(CustomerApi.class);

    private @Autowired CustomerService customerService;

    @RequestMapping(method = RequestMethod.GET)
    public List<CustomerEntity> get(@RequestParam(value = "tenant", required = true) String tenant) {
        log.info("Inside get");
        return customerService.get();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public CustomerEntity get(@RequestParam(value = "tenant", required = true) String tenant,
            @PathVariable("id") Long id) {
        log.info("Inside get by Id");
        return customerService.get(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Response create(@RequestParam(value = "tenant", required = true) String tenant,
            @RequestBody CustomerEntity customer) {
        log.info("Inside create");
        return customerService.create(customer);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Response update(@RequestParam(value = "tenant", required = true) String tenant,
            @RequestBody CustomerEntity customer) {
        log.info("Inside update");
        return customerService.update(customer);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Response delete(@RequestParam(value = "tenant", required = true) String tenant, @PathVariable Long id) {
        log.info("Inside delete");
        return customerService.delete(id);
    }

}
