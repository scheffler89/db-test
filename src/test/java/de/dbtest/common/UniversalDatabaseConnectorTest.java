package de.dbtest.common;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UniversalDatabaseConnectorTest {

    @Test
    void getIdentifier() {
        UniversalDatabaseConnector target = new UniversalDatabaseConnector(
                UniversalDatabaseConnector.DBT_MYSQL,
                "localhost",
                3306,
                "information_schema",
                "root",
                "root"
        );

        assertEquals("root@localhost:3306/information_schema", target.getIdentifier());
    }

}