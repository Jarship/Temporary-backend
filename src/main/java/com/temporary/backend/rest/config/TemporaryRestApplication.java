package com.temporary.backend.rest.config;

import javax.sql.DataSource;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import com.temporary.backend.rest.*;

public class TemporaryRestApplication extends Application {
    private final Set<Object> singletons;
    protected DataSource dataSource;
    protected Logger logger = Logger.getLogger(TemporaryRestApplication.class.getSimpleName());

    public TemporaryRestApplication() {
        logger.info("---- init TemporaryRestApplication ----");
        singletons = new HashSet<>();
        singletons.add(new TestRestService());
        singletons.add(new AccountRestService());
        singletons.add(new AssemblyRestService());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
