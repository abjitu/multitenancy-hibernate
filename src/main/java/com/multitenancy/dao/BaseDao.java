package com.multitenancy.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.IdentifierLoadAccess;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.multitenancy.entity.AbstractEntity;

public class BaseDao<E, PK extends Serializable> {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private Class<E> persistentClass;

    @Autowired
    protected SessionFactory sessionFactory;

    public BaseDao(final Class<E> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public Session getSession() throws HibernateException {
        Session sess = sessionFactory.getCurrentSession();
        if (sess == null) {
            sess = sessionFactory.openSession();
        }
        return sess;
    }
    
    @SuppressWarnings("unchecked")
    public List<E> getAllB() {
        StatelessSession sess = sessionFactory.openStatelessSession();
        Transaction tx = sess.beginTransaction();
        List<E> result = sess.createCriteria(persistentClass).list();
        tx.commit();
        sess.close();
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<E> getAll() {
        Session sess = getSession();
        return sess.createCriteria(persistentClass).list();
    }

    public List<E> getAllDistinct() {
        Collection<E> result = new LinkedHashSet<E>(getAll());
        return new ArrayList<E>(result);
    }

    @SuppressWarnings("unchecked")
    public E get(PK id) {
        Session sess = getSession();
        IdentifierLoadAccess byId = sess.byId(persistentClass);
        E entity = (E) byId.load(id);

        if (entity == null) {
            log.warn("Uh oh, '" + this.persistentClass + "' object with id '" + id + "' not found...");
        }

        return entity;
    }

    @SuppressWarnings("unchecked")
    public boolean exists(PK id) {
        Session sess = getSession();
        IdentifierLoadAccess byId = sess.byId(persistentClass);
        E entity = (E) byId.load(id);
        return entity != null;
    }

    public E save(E object) {
        if (log.isDebugEnabled()) {
            log.debug(persistentClass + " : " + object);
        }
        if(object instanceof AbstractEntity){
            ((AbstractEntity) object).onCreate();
        }
        getSession().saveOrUpdate(object);
        // necessary to throw a DataIntegrityViolation and catch it in
        // UserManager
        getSession().flush();
        return object;
    }


    @SuppressWarnings("unchecked")
    public E update(E object) {
        if(object instanceof AbstractEntity){
            ((AbstractEntity) object).onUpdate();
        }
        return (E) getSession().merge(object);
    }

    public void remove(E object) {
        Session sess = getSession();
        sess.delete(object);
    }

    @SuppressWarnings("unchecked")
    public void remove(PK id) {
        Session sess = getSession();
        IdentifierLoadAccess byId = sess.byId(persistentClass);
        E entity = (E) byId.load(id);
        sess.delete(entity);
    }

    @SuppressWarnings("rawtypes")
    public List getByNamedQuery(String queryName) {
        return getByNamedQuery(queryName, null, null, null);
    }

    @SuppressWarnings("rawtypes")
    public List getByNamedQuery(String queryName, Map<String, Object> queryParams) {
        return getByNamedQuery(queryName, queryParams, null, null);
    }

    @SuppressWarnings("rawtypes")
    public List getByNamedQuery(String queryName, Map<String, Object> queryParams, Integer page, Integer size) {
        Session sess = getSession();
        Query namedQuery = sess.getNamedQuery(queryName);
        if (null != queryParams) {
            for (String s : queryParams.keySet()) {
                Object obj = queryParams.get(s);
                if (obj instanceof Collection<?>)
                    namedQuery.setParameterList(s, (Collection) obj);
                else
                    namedQuery.setParameter(s, obj);
            }
        }
        if (page != null && page > 0 && size != null) {
            namedQuery.setFirstResult((page - 1) * size);
            namedQuery.setMaxResults(size);
        }
        return namedQuery.list();
    }

    @SuppressWarnings("rawtypes")
    public List executeQuery(String hql, Map<String, Object> queryParams) {
        Session sess = getSession();
        Query query = sess.createQuery(hql);
        query.setProperties(queryParams);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<E> findByProperty(String propertyName, Object value) {
        log.debug("finding " + this.persistentClass + " instance with property: " + propertyName + ", value: " + value);
        List<E> list = null;
        try {
            Query query = getSession().createQuery(
                    "from " + this.persistentClass.getName() + " model where model." + propertyName + " = :"
                            + propertyName);
            query.setParameter(propertyName, value);
            list = query.list();
        } catch (RuntimeException re) {
            log.error("find by property name failed", re);
        }
        return list;
    }

    public E findUniqueByProperty(String propertyName, Object value) {
        log.debug("finding Unique " + this.persistentClass + " instance with property: " + propertyName + ", value: "
                + value);
        E result = null;
        List<E> list = findByProperty(propertyName, value);
        if (null != list && list.size() == 1) {
            result = list.get(0);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<E> findByExample(E object) {
        log.debug("finding " + this.persistentClass + " instance by example");
        try {
            Session session = sessionFactory.getCurrentSession();
            Example example = Example.create(object);
            List<E> results = session.createCriteria(this.persistentClass).add(example).list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        } catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    }

    public List<E> findByNamedQuery(String queryName) {
        return findByNamedQuery(queryName, null, null, null);
    }

    public List<E> findByNamedQuery(String queryName, Map<String, Object> queryParams) {
        return findByNamedQuery(queryName, queryParams, null, null);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<E> findByNamedQuery(String queryName, Map<String, Object> queryParams, Integer page, Integer size) {
        Session sess = getSession();
        Query namedQuery = sess.getNamedQuery(queryName);
        if (null != queryParams) {
            for (String s : queryParams.keySet()) {
                Object obj = queryParams.get(s);
                if (obj instanceof Collection<?>)
                    namedQuery.setParameterList(s, (Collection) obj);
                else
                    namedQuery.setParameter(s, obj);
            }
        }
        if (page != null && page > 0 && size != null) {
            namedQuery.setFirstResult((page - 1) * size);
            namedQuery.setMaxResults(size);
        }
        namedQuery.setResultTransformer(new AliasToBeanResultTransformer(persistentClass));
        return namedQuery.list();
    }

}