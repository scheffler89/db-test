package de.dbtest.cli;

import de.dbtest.common.TestCase;
import de.dbtest.common.UniversalDatabaseConnector;
import de.dbtest.persistence.Configuration;
import de.dbtest.persistence.FilePersistence;
import de.dbtest.persistence.StatusItem;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;
import java.util.Set;

/**
 * @author Lennard Scheffler
 */
public class CmdClient {

    private static final Logger LOGGER = LogManager.getLogger(CmdClient.class);

    public static void main(String[] args) throws ParseException {

        Options options = initOptions();

        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine commandLine = commandLineParser.parse(options, args);

        String projectRootDirectory = System.getProperty("user.dir");
        if (commandLine.hasOption("root")) {
            projectRootDirectory = commandLine.getOptionValue("root");
        }

        LOGGER.info("Project root is {}", projectRootDirectory);

        if (commandLine.hasOption("execute") && commandLine.hasOption("case") && commandLine.hasOption("target")) {
            String testCaseIdentifier = commandLine.getOptionValue("case");
            String targetIdentifier = commandLine.getOptionValue("target");
            LOGGER.info("Execute Test Case {} at target {}", testCaseIdentifier, targetIdentifier);
            cmdExecuteTestCase(testCaseIdentifier, targetIdentifier, projectRootDirectory);
        }
        else if (commandLine.hasOption("execute") && commandLine.hasOption("set") && commandLine.hasOption("target")) {
            String testSetIdentifier = commandLine.getOptionValue("set");
            String targetIdentifier = commandLine.getOptionValue("target");
            LOGGER.info("Execute Test Case Set {} at target {}", testSetIdentifier, targetIdentifier);
            cmdExecuteTestSet(testSetIdentifier, targetIdentifier, projectRootDirectory);
        }
        else if (commandLine.hasOption("status") && commandLine.hasOption("case")) {
            String testCaseIdentifier = commandLine.getOptionValue("case");
            LOGGER.info("Get status of Test Case {}" + testCaseIdentifier);
            cmdStatusCase(testCaseIdentifier, projectRootDirectory);
        }
        else if (commandLine.hasOption("status") && commandLine.hasOption("set")) {
            String testSetIdentifier = commandLine.getOptionValue("set");
            LOGGER.info("Get status of Test Case Set {}", testSetIdentifier);
            cmdStatusSet(testSetIdentifier, projectRootDirectory);
        }
        else if (commandLine.hasOption("addtarget")) {
            LOGGER.info("Add a new target.");
            cmdAddTarget();
        }
        else {
            LOGGER.info("No valid parameter combination.");
            cmdHelp(options);
        }

    }

    public static Options initOptions() {
        Options options = new Options();

        options.addOption("help", false, "Print this message.");
        options.addOption("execute", false, "Execute a single test case or a set of test cases.");
        options.addOption("status", false, "Get the status of a single test case or a set of test cases.");
        options.addOption("case", true, "Execute a single test case or a set of test cases.");
        options.addOption("set", true, "Execute multiple test cases within a test set.");
        options.addOption("target", true, "Target identifier.");
        options.addOption("root", true, "Define the project root directory.");

        return options;
    }

    public static void cmdHelp(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("dwplus [options]", options);
    }


    public static void cmdExecuteTestCase(String testCaseIdentifier, String targetIdentifier, String projectRootDirectory) {

        try {
            FilePersistence persistence = new FilePersistence(projectRootDirectory);

            Configuration config = persistence.loadConfiguration();
            UniversalDatabaseConnector target = config.getTarget(targetIdentifier);
            TestCase testCase = persistence.load(testCaseIdentifier);

            testCase.execute(target);
            printTestResult(testCase, target);
            persistence.saveStatus(testCase);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cmdExecuteTestSet(String testSetIdentifier, String targetIdentifier, String projectRootDirectory) {

        try {
            FilePersistence persistence = new FilePersistence(projectRootDirectory);

            Configuration config = persistence.loadConfiguration();
            UniversalDatabaseConnector target = config.getTarget(targetIdentifier);


            Set<TestCase> testCases = persistence.loadMultiple(testSetIdentifier);

            for (TestCase t : testCases) {
                t.execute(target);
                printTestResult(t, target);
                persistence.saveStatus(t);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void printTestResult(TestCase testCase, UniversalDatabaseConnector target) {
        printTestStatus(testCase.getIdentifier(), target.getIdentifier(), testCase.getStatus(target.getIdentifier()));
    }

    public static void printTestStatus(StatusItem statusItem) {
        printTestStatus(statusItem.getTestCaseIdentifier(), statusItem.getTargetIdentifier(), statusItem.getStatus());
    }

    public static void printTestStatus(String testCaseIdentifier, String targetIdentifier, int statusIndicator) {
        String message = "Test case [" + testCaseIdentifier + "] executed at target [" + targetIdentifier + "]: " + translateStatusIndicator(statusIndicator);
        if (statusIndicator == TestCase.TCS_FAILED || statusIndicator == TestCase.TCS_STOPPED) {
            System.out.println(message);
        }
        else {
            System.err.println(message);
        }
    }


    public static void cmdStatusCase(String testCaseIdentifier, String projectRootDirectory) {
        try {
            FilePersistence persistence = new FilePersistence(projectRootDirectory);
            Set<StatusItem> statusItemSet = persistence.loadStatus(testCaseIdentifier);

            for (StatusItem statusItem : statusItemSet) {
                printTestStatus(statusItem);
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void cmdStatusSet(String testSetIdentifier, String projectRootDirectory) {
        try {
            FilePersistence persistence = new FilePersistence(projectRootDirectory);

            Set<TestCase> testCases = persistence.loadMultiple(testSetIdentifier);

            for (TestCase t : testCases) {
                cmdStatusCase(t.getIdentifier(), projectRootDirectory);
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static String translateStatusIndicator(int statusIndicator) {
        if (statusIndicator == TestCase.TCS_PASSED)
            return "PASSED";

        if (statusIndicator == TestCase.TCS_FAILED)
            return "FAILED";

        if (statusIndicator == TestCase.TCS_PENDING)
            return "PENDING";

        if (statusIndicator == TestCase.TCS_RUNNING)
            return "RUNNING";

        if (statusIndicator == TestCase.TCS_STOPPED)
            return "STOPPED";


        return "UNKNOWN";

    }

    private static void cmdAddTarget() {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Database Type:");
        String databaseType = scanner.next();

        System.out.print("Host:");
        String host = scanner.next();

        System.out.print("Port:");
        int port = scanner.nextInt();

        System.out.print("Database / Schema:");
        String database = scanner.next();

        System.out.print("Username:");
        String username = scanner.next();

        System.out.print("Password:");
        String password = scanner.next();

        System.out.println();

        System.out.print("Would you like to add the Target? (yes)");
        String userConfirmation = scanner.next();

        if (userConfirmation.equalsIgnoreCase("yes") || userConfirmation.isEmpty()) {
            UniversalDatabaseConnector connector = new UniversalDatabaseConnector();
        }

    }
}
