package com.multitenancy.serializer;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JacksonCommonsModule extends SimpleModule {
    private static final long serialVersionUID = 1L;

    public JacksonCommonsModule() {
        super("JacksonMultiTenancyModule", Version.unknownVersion());

        addSerializer(DateTime.class, new JodaDateSerializer());
    }
}
