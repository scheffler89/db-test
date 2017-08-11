package de.dbtest.dummies;

public class TestCaseDummies {

    public static final String TEST_CASE_NAME = "TestCase";
    public static final String TEST_CASE_PACKAGE = "de.tests";

    public static final String TEST_CASE_HEAD = "/**" + System.getProperty("line.separator")
            + "* @package " + TEST_CASE_PACKAGE + System.getProperty("line.separator")
            + "* @test " + TEST_CASE_NAME + System.getProperty("line.separator")
            + "*/";

    public static final String TEST_CASE_BODY_PASSED = TEST_CASE_HEAD + System.getProperty("line.separator")
            + "select 'passed' as result;";

    public static final String TEST_CASE_BODY_FAILED = TEST_CASE_HEAD + System.getProperty("line.separator")
            + "select 'failed' as result;";

    public static final String TEST_CASE_BODY_SQL_ERROR = TEST_CASE_HEAD + System.getProperty("line.separator")
            + "sel nonsense;";
}
