package de.dbtest.common;

import de.dbtest.dummies.TargetDummies;
import de.dbtest.dummies.TestCaseDummies;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


class TestSetTest {

    @Test
    public void add() {
        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        TestSet testSet = new TestSet();
        assertTrue(testSet.add(testCase));
    }

    @Test
    public void addAll() {
        TestCase testCase1 = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        TestCase testCase2 = new TestCase(TestCaseDummies.TEST_CASE_BODY_FAILED);

        Set<TestCase> testCases = new HashSet<TestCase>();
        testCases.add(testCase1);
        testCases.add(testCase2);

        TestSet testSet = new TestSet();
        assertTrue(testSet.addAll(testCases));

        assertTrue(testSet.getTestCases().contains(testCase1));
        assertTrue(testSet.getTestCases().contains(testCase2));
    }

    @Test
    public void constructorFromTestCaseCollection() {
        TestCase testCase1 = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        TestCase testCase2 = new TestCase(TestCaseDummies.TEST_CASE_BODY_FAILED);

        Set<TestCase> testCases = new HashSet<TestCase>();
        testCases.add(testCase1);
        testCases.add(testCase2);

        TestSet testSet = new TestSet(testCases);

        assertTrue(testSet.getTestCases().contains(testCase1));
        assertTrue(testSet.getTestCases().contains(testCase2));
    }

    @Test
    public void remove() {
        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        TestSet testSet = new TestSet();
        testSet.add(testCase);
        assertTrue(testSet.remove(testCase));
    }

    @Test
    public void get() {
        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        TestSet testSet = new TestSet();
        testSet.add(testCase);

        assertEquals(testCase, testSet.get("de.tests.TestCase"));
        assertNull(testSet.get("something"));
    }

    @Test
    public void getTestCasesSubset() {
        TestSet testSet = new TestSet();

        TestCase testCase1 = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        testSet.add(testCase1);

        TestCase testCase2 = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        testSet.add(testCase2);

        TestCase testCase3 = new TestCase(TestCaseDummies.TEST_CASE_BODY_FAILED);
        testSet.add(testCase3);

        UniversalDatabaseConnector target = TargetDummies.getDefaultTarget();

        testSet.execute(target);

        Set<TestCase> allTestCases = testSet.getTestCases(TestCase.TCS_PASSED, target.getIdentifier());
        assertTrue(allTestCases.contains(testCase1));
        assertTrue(allTestCases.contains(testCase2));
        assertFalse(allTestCases.contains(testCase3));
    }

    @Test
    public void getAllTestCases() {
        TestSet testSet = new TestSet();

        TestCase testCase1 = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        testSet.add(testCase1);

        TestCase testCase2 = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        testSet.add(testCase2);

        TestCase testCase3 = new TestCase(TestCaseDummies.TEST_CASE_BODY_FAILED);
        testSet.add(testCase3);

        Set<TestCase> allTestCases = testSet.getTestCases();
        assertTrue(allTestCases.contains(testCase1));
        assertTrue(allTestCases.contains(testCase2));
        assertTrue(allTestCases.contains(testCase3));
    }

    @Test
    public void executeAllPassed() {
        TestSet testSet = new TestSet();

        TestCase testCase1 = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        testSet.add(testCase1);

        TestCase testCase2 = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        testSet.add(testCase2);

        TestCase testCase3 = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        testSet.add(testCase3);

        UniversalDatabaseConnector target =TargetDummies.getDefaultTarget();

        assertTrue(testSet.execute(target));
    }

    @Test
    public void executeAllPassedAndFailed() {
        TestSet testSet = new TestSet();

        TestCase testCase1 = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        testSet.add(testCase1);

        TestCase testCase2 = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        testSet.add(testCase2);

        TestCase testCase3 = new TestCase(TestCaseDummies.TEST_CASE_BODY_FAILED);
        testSet.add(testCase3);

        UniversalDatabaseConnector target = TargetDummies.getDefaultTarget();

        assertFalse(testSet.execute(target));
    }

    @Test
    public void executeParallel() {
        TestSet testSet = new TestSet();

        for (int i = 0; i < 1000; i++) {
            TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
            testSet.add(testCase);
        }

        assertEquals(1000, testSet.getTestCases().size());

        UniversalDatabaseConnector target = TargetDummies.getDefaultTarget();

        testSet.executeParallel(target, 5, 60, TimeUnit.SECONDS);
    }


}