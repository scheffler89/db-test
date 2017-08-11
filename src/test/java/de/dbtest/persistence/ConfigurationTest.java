package de.dbtest.persistence;

import de.dbtest.common.UniversalDatabaseConnector;
import de.dbtest.dummies.TargetDummies;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTest {

    @Test
    public void addTarget() {
        Configuration config = new Configuration();
        assertTrue(config.addTarget(TargetDummies.getDefaultTarget()));
    }

    @Test
    public void getTarget() {
        UniversalDatabaseConnector target = TargetDummies.getDefaultTarget();
        Configuration config = new Configuration();
        config.addTarget(target);
        assertEquals(target, config.getTarget(target.getIdentifier()));
    }

    @Test
    public void removeTarget() {
        UniversalDatabaseConnector target = TargetDummies.getDefaultTarget();
        Configuration config = new Configuration();
        config.addTarget(target);
        assertEquals(target, config.getTarget(target.getIdentifier()));

        assertTrue(config.removeTarget(target.getIdentifier()));
        assertNull(config.getTarget(target.getIdentifier()));
    }




}