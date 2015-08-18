package com.multitenancy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.multitenancy.persistence.usertype.JsonbUserType;

@SqlResultSetMapping(name = "findByJsonB3", entities = @EntityResult(entityClass = CustomerEntity.class))
@NamedQueries({ @NamedQuery(name = "findByEmail",
        query = "select c.id as id,c.pattern as pattern from CustomerEntity c where c.email = :email") })
@NamedNativeQueries({
        @NamedNativeQuery(name = "findByJsonB1", query = "select * from customer where pattern \\?\\? 'total'",
                resultClass = CustomerEntity.class),
        @NamedNativeQuery(name = "findByJsonB2", query = "select * from customer where pattern @> '{}'",
                resultClass = CustomerEntity.class),
        @NamedNativeQuery(name = "findByJsonB3", query = "select * from customer where pattern @> '{}'",
                resultSetMapping = "findByJsonB3") })
@TypeDef(name = "jsonb", typeClass = JsonbUserType.class, parameters = { @Parameter(name = JsonbUserType.CLASS,
        value = "com.multitenancy.entity.Patterns") })
@Entity
@Table(name = "customer")
@DynamicUpdate
public class CustomerEntity extends AbstractEntity {
    private String firstName;
    private String lastName;
    private String email;

    private Patterns pattern;

    @Column(name = "first_name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "last_name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Type(type = "jsonb")
    @Column(name = "pattern")
    public Patterns getPattern() {
        return pattern;
    }

    public void setPattern(Patterns pattern) {
        this.pattern = pattern;
    }

}
