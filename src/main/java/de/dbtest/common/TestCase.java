package de.dbtest.common;

import de.dbtest.persistence.StatusItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A test is an element that can be executed on a specific database target and provides information about the database.
 * This may refer to the structure or the content. A test case is usually represented by a SQL query returning a field
 * called result and a comment header including general information about the test case. <br />
 * A test case is always parsed from a text file containing the header and the query (called test case body).
 *
 * @author Lennard Scheffler
 */
public class TestCase extends Observable implements Executable {

    /* STATUS INDICATORS **********************************************************************************************/

    public static final int TCS_PENDING         = 0;
    public static final int TCS_RUNNING         = 1;
    public static final int TCS_STOPPED         = 2;
    public static final int TCS_PASSED          = 3;
    public static final int TCS_FAILED          = 4;


    /* ANNOTATION INFORMATION *****************************************************************************************/

    /**
     * Annotation used for indicating the test case package.
     */
    public static final String ANNOT_PACKAGE = "@package";

    /**
     * Annotation used for indicating a test case name.
     */
    public static final String ANNOT_NAME = "@test";


    /* BODY PARSING EXPRESSIONS ***************************************************************************************/

    /**
     * Regular Expression that can be used for extracting the header out of a test case body.
     */
    public static final String REGEXP_HEAD = "(\\/\\*{2}(\\r\\n|\\n)((.|\\r\\n|\\n)*)\\*\\/)";

    /**
     * Regular Expression that can be used for extracting the query out ot the test case body.
     */
    public static final String REGEXP_QUERY = "\\*\\/((.|\\r\\n|\\n)*);";

    /**
     * Regular Expression that can be used for extrcting the test case package out of the header.
     */
    public static final String REGEXP_PACKAGE = ANNOT_PACKAGE + " (.*)";

    /**
     * Regular Expression that can be used for extrcting the test case name out of the header.
     */
    public static final String REGEXP_NAME = ANNOT_NAME + " (.*)";


    /* Private POJO attributes ****************************************************************************************/

    private String body;
    private Map<String, Integer> status;



    /**
     * Create a test case based on a text body.
     *
     * @param body Full test case body.
     */
    public TestCase(String body) {
        this.body = body;
        this.status = new HashMap<String, Integer>();
    }

    /**
     * Get the header of the test case body. A header is identified by a multi-line documentation comment and contains
     * the specific test case annotations like @test or @package.
     *
     * @return Full header including multi-line comment elements.
     */
    public String getHead() {
        Pattern headPattern = Pattern.compile(REGEXP_HEAD);
        Matcher headMatcher = headPattern.matcher(body);

        if (headMatcher.find()) {
            return headMatcher.group(1).trim();
        }

        return null;
    }

    /**
     * Get the package from the header. The package declaration has to be placed inside the header. It is identified by
     * the annotation @package.
     *
     * @return The full package name of the test case (without identifying annotion).
     */
    public String getPackage() {
        if (getHead() == null) {
            return null;
        }

        Pattern packagePattern = Pattern.compile(REGEXP_PACKAGE);
        Matcher packageMatcher = packagePattern.matcher(getHead());

        if (packageMatcher.find()) {
            return packageMatcher.group(1).trim();
        }

        return null;
    }

    /**
     * Get the name of the test case from the body. The name declaration has to be inside the header. It is identified
     * by the annotation @test.
     *
     * @return The full name of the test case (without identifying annotation).
     */
    public String getName() {
        if (getHead() == null) {
            return null;
        }

        Pattern namePattern = Pattern.compile(REGEXP_NAME);
        Matcher nameMatcher = namePattern.matcher(getHead());

        if (nameMatcher.find()) {
            return nameMatcher.group(1).trim();
        }

        return null;
    }

    /**
     * Get the query from the test case body. The query does not need any additional declaration or annoation.
     *
     * @return query that can be executed.
     */
    public String getQuery() {
        Pattern queryPattern = Pattern.compile(REGEXP_QUERY);
        Matcher queryMatcher = queryPattern.matcher(body);

        if (queryMatcher.find()) {
            return queryMatcher.group(1).trim();
        }

        return null;
    }

    /**
     * Set the test query within the test case body. A test query has to be well formatted and return a column named
     * result indicating the result of the test case.
     *
     * @param query Well formatted test query.
     */
    public void setQuery(String query) {
        body = getHead() + System.getProperty("line.separator") + query;
    }

    /**
     * Get the full identifier of the test case. The identifer is the combination of the test case package and the test
     * case name.
     *
     * @return Identifier including package and name.
     */
    public String getIdentifier() {
        return getPackage() + "." + getName();
    }

    /**
     * Get the current status of a test case. A status is always connected to a specific target. The test case may have
     * various statuses referring to different targets.
     *
     * @param targetIdentifier Identifyer of the target. A target identifyier is usually made up from the user,
     *                         host/port and database that it connects to.
     *
     * @return Status at the given target or null if the test case has never been executed at the given target.
     */
    public int getStatus(String targetIdentifier) {
        return status.get(targetIdentifier);
    }

    /**
     * Set the status of the test case for a specific target. A status is always connected to a specific target. The
     * test case may have various statuses referring to different targets.
     *
     * @param targetIdentifier Identifyer of the target. A target identifyier is usually made up from the user,
     *                         host/port and database that it connects to.
     * @param statusIndicator The status indicator is a numeric value representing the status of the test case. The
     *                        available statuses are captured within static attributes of the class.
     */
    public void setStatus(String targetIdentifier, int statusIndicator) {
        status.put(targetIdentifier, statusIndicator);
        setChanged();
        notifyObservers();
    }

    /**
     * Get the full body of the test case. The body usually consists out of the header and the test query.
     * @return The full body of the test case.
     */
    public String getBody() {
        return body;
    }

    /**
     * Set the full body of the test case.
     * @param body Well formatted body of the test case.
     */
    public void setBody(String body) {
        this.body = body;
    }


    @Override
    public boolean execute(UniversalDatabaseConnector target) {
        setStatus(target.getIdentifier(), TCS_RUNNING);

        int resultStatus = TCS_FAILED;

        Connection connection = target.connect();
        if (connection != null) {
            try {
                // Try to execute the test statement at the target and grep the return result-parameter.

                PreparedStatement statement = connection.prepareStatement(getQuery());
                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    if (result.getString("result").toLowerCase().equals("passed")) {
                        resultStatus = TCS_PASSED;
                    } else {
                        resultStatus = TCS_FAILED;
                    }
                }
            } catch (SQLException e) {
                // Return "Stopped" if the test cases results in an SQL error.
                e.printStackTrace();
                resultStatus = TCS_STOPPED;
            }
        }

        setStatus(target.getIdentifier(), resultStatus);

        // Try to close the connection before exiting the method
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return (resultStatus == TCS_PASSED);
    }

    public Set<StatusItem> getStatusSet() {
        Set<StatusItem> statusItemSet = new HashSet<StatusItem>();

        for (String targetIdentifier : status.keySet()) {
            StatusItem statusItem = new StatusItem(getIdentifier(), targetIdentifier, status.get(targetIdentifier));
            statusItemSet.add(statusItem);
        }

        return statusItemSet;
    }

    public void setStatus(Set<StatusItem> statusItemSet) {
        for (StatusItem statusItem : statusItemSet) {
            setStatus(statusItem.getTargetIdentifier(), statusItem.getStatus());
        }
    }
}
