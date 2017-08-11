package de.dbtest.common;

/**
 * @author Lennard Scheffler
 */
public interface Executable {

    /**
     * Execute the executable object at a specific target. Executing the test case will run the build up a database
     * connection to the target and run the test query at the given target. Executing the test case will cause the
     * status identifier to change referring to the current status of the test case.
     *
     * @param target Target object that is used for building a connection to the target database.
     * @return (1) true, if the exutable object (or all nested objects) has been executed successfully with the result
     * "passed" or false if the test case has been executed with the result "failed" or executing the test case caused
     * any kind of SQL error.
     */
    public boolean execute(UniversalDatabaseConnector target);

}
