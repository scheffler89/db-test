package de.dbtest.cli;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class CmdClientTest {

    private static final String PROJECT_ROOT_DIRECTORY = "/opt/dbtest";

    @Test
    public void cmdExecuteTestCase() throws IOException {
        CmdClient.cmdExecuteTestCase("de.tests.TestCase", "root@localhost:3306/information_schema", PROJECT_ROOT_DIRECTORY);
    }

    @Test
    public void cmdExecuteTestSet() throws IOException {
        CmdClient.cmdExecuteTestSet("de.tests", "root@localhost:3306/information_schema", PROJECT_ROOT_DIRECTORY);
    }

}