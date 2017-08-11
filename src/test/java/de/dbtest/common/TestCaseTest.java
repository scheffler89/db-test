package de.dbtest.common;

import de.dbtest.dummies.TargetDummies;
import de.dbtest.dummies.TestCaseDummies;
import de.dbtest.persistence.StatusItem;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Observer;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

class TestCaseTest {

    @Test
    public void getHead() {
        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        assertEquals(TestCaseDummies.TEST_CASE_HEAD, testCase.getHead());
    }

    @Test
    public void getPackage() {
        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        assertEquals("de.tests", testCase.getPackage());
    }

    @Test
    public void getName() {
        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        assertEquals("TestCase", testCase.getName());
    }

    @Test
    public void getIdentifier() {
        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        assertEquals("de.tests.TestCase", testCase.getIdentifier());
    }

    @Test
    public void getQuery() {
        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        assertEquals("select 'passed' as result", testCase.getQuery());
    }

    @Test
    public void getEmpties() {
        TestCase testCase = new TestCase("");
        assertNull(testCase.getHead());
        assertNull(testCase.getName());
        assertNull(testCase.getPackage());
    }

    @Test
    public void setQuery() {
        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        testCase.setQuery("select 'failed' as result;");
        assertEquals("select 'failed' as result", testCase.getQuery());
    }

    @Test
    public void getAndSetStatus() {
        UniversalDatabaseConnector target = TargetDummies.getDefaultTarget();
        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        testCase.setStatus(target.getIdentifier(), TestCase.TCS_PASSED);
        assertEquals(TestCase.TCS_PASSED, testCase.getStatus(target.getIdentifier()));
    }

    @Test
    public void setStatusFromSet() {
        Set<StatusItem> statusItemSet = new HashSet<StatusItem>();

        StatusItem statusItem1 = new StatusItem("de.tests.TestCase", "user@host:1234/database", TestCase.TCS_PASSED);
        statusItemSet.add(statusItem1);

        StatusItem statusItem2 = new StatusItem("de.tests.TestCase", "user@host2:1234/database", TestCase.TCS_PASSED);
        statusItemSet.add(statusItem2);

        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);

        testCase.setStatus(statusItemSet);

        assertEquals(TestCase.TCS_PASSED, testCase.getStatus("user@host:1234/database"));
    }

    @Test
    public void executePassed() {
        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        UniversalDatabaseConnector target = TargetDummies.getDefaultTarget();

        assertTrue(testCase.execute(target));
        assertEquals(TestCase.TCS_PASSED, testCase.getStatus(target.getIdentifier()));
    }

    @Test
    public void executeFailed() {
        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_FAILED);
        UniversalDatabaseConnector target = TargetDummies.getDefaultTarget();

        assertFalse(testCase.execute(target));
        assertEquals(TestCase.TCS_FAILED, testCase.getStatus(target.getIdentifier()));
    }

    @Test
    public void executeSQLError() {
        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_SQL_ERROR);
        UniversalDatabaseConnector target = TargetDummies.getDefaultTarget();

        assertFalse(testCase.execute(target));
        assertEquals(TestCase.TCS_STOPPED, testCase.getStatus(target.getIdentifier()));
    }

    @Test
    public void observe() {
        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        UniversalDatabaseConnector target = TargetDummies.getDefaultTarget();

        Observer observer = Mockito.spy(Observer.class);
        testCase.addObserver(observer);

        testCase.execute(target);

        Mockito.verify(observer, Mockito.atLeast(2)).update(testCase, null);

    }

    @Test
    public void getStatusSet() {
        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        testCase.setStatus("user@host1:1234/database", TestCase.TCS_PASSED);
        testCase.setStatus("user@host2:1234/database", TestCase.TCS_PASSED);
        testCase.setStatus("user@host3:1234/database", TestCase.TCS_PASSED);

        Set<StatusItem> statusItemSet = testCase.getStatusSet();
        assertEquals(3, statusItemSet.size());

        assertTrue(statusItemSet.contains(
                new StatusItem(
                        testCase.getIdentifier(),
                        "user@host1:1234/database",
                        TestCase.TCS_PASSED)
                )
        );
    }

}