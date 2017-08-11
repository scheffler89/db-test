package de.dbtest.common;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A test set captures and manages multiple test cases. When executing a test set all included test cases will be executed. While executing
 *
 * @author Lennard Scheffler
 */
public class TestSet implements Observer, Executable {

    private Set<TestCase> testCases;

    public TestSet() {
        this.testCases = new HashSet<TestCase>();
    }

    public TestSet(Collection<TestCase> testCases) {
        this();
        addAll(testCases);
    }

    public void update(Observable observable, Object o) {

    }

    public boolean add(TestCase testCase) {
        testCase.addObserver(this);
        return this.testCases.add(testCase);
    }

    public boolean addAll(Collection<TestCase> testCases) {
        boolean result = true;
        for (TestCase t : testCases) {
            result &= add(t);
        }

        return result;
    }

    public boolean remove(TestCase testCase) {
        testCase.deleteObserver(this);
        return this.testCases.remove(testCase);
    }

    public TestCase get(String identifier) {
        //noinspection Since15
        List<TestCase> l = testCases.stream().filter(s -> s.getIdentifier().equals(identifier)).collect(Collectors.toList());

        if (l.size() == 0) return null;

        return l.get(0);
    }

    @Override
    public boolean execute(UniversalDatabaseConnector target) {
        boolean testResult = true;
        for(TestCase t : testCases) {
            testResult = testResult && t.execute(target);
        }
        return testResult;
    }

    public void executeParallel(UniversalDatabaseConnector target, int parallel, long timeout, TimeUnit timeUnit) {
        ExecutorService threadPool = Executors.newFixedThreadPool(parallel);

        for(TestCase t : testCases) {
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    t.execute(target);
                }
            });
        }

        threadPool.shutdown();

        try {
            threadPool.awaitTermination(timeout, timeUnit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public Set<TestCase> getTestCases() {
        return testCases;
    }

    public Set<TestCase> getTestCases(int status, String targetIdentifier) {
        return testCases.stream().filter(s -> s.getStatus(targetIdentifier) == status).collect(Collectors.toSet());
    }
}
