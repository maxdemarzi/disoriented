package com.maxdemarzi;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class HealthCheckTest {

    private Disoriented db;

    @Before
    public void setup() throws IOException {
        Disoriented.init();
        db = Disoriented.getInstance();
        db.clear();
    }

    @Test
    public void shouldBeHealthy() throws Exception {
        DatabaseHealthCheck check = new DatabaseHealthCheck();
        assertTrue(check.check().isHealthy());
    }

}
