package com.multitenancy.persistence.dialect;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL9Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class JsonbPostgreSQLDialect extends PostgreSQL9Dialect {

    public JsonbPostgreSQLDialect() {

        super();
        this.registerColumnType(Types.JAVA_OBJECT, "json");
        registerFunction( "make_interval", new StandardSQLFunction("make_interval", StandardBasicTypes.TIMESTAMP) );
        registerFunction( "make_timestamp", new StandardSQLFunction("make_timestamp", StandardBasicTypes.TIMESTAMP) );
        registerFunction( "make_timestamptz", new StandardSQLFunction("make_timestamptz", StandardBasicTypes.TIMESTAMP) );
        registerFunction( "make_date", new StandardSQLFunction("make_date", StandardBasicTypes.DATE) );
        registerFunction( "make_time", new StandardSQLFunction("make_time", StandardBasicTypes.TIME) );
    }
}