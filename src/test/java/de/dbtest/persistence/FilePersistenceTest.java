package de.dbtest.persistence;

import com.mysql.cj.core.log.LogFactory;
import de.dbtest.common.TestCase;
import de.dbtest.dummies.TargetDummies;
import de.dbtest.dummies.TestCaseDummies;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilePersistenceTest {

    private static final String PROJECT_ROOT_DIRECTORY = "/opt/dbtest";

    @Test
    public void create() {
        FilePersistence persistence = new FilePersistence(PROJECT_ROOT_DIRECTORY);
    }

    @Test
    public void initialize() throws IOException {
        FilePersistence persistence = new FilePersistence(PROJECT_ROOT_DIRECTORY);

        persistence.initialize();

        Path projectConfigurationDirectory = Paths.get(persistence.getConfigDirectory());
        assertTrue(Files.exists(projectConfigurationDirectory));
    }

    @Test
    public void getConfigDirectory() {
        FilePersistence persistence = new FilePersistence(PROJECT_ROOT_DIRECTORY);
        assertEquals(PROJECT_ROOT_DIRECTORY + "/.dwplus", persistence.getConfigDirectory());
    }

    @Test
    public void getConfigFile() {
        FilePersistence persistence = new FilePersistence(PROJECT_ROOT_DIRECTORY);
        assertEquals(PROJECT_ROOT_DIRECTORY + "/dwplus.json", persistence.getConfigFile());
    }

    @Test
    public void getStatusFile() {
        FilePersistence persistence = new FilePersistence(PROJECT_ROOT_DIRECTORY);
        assertEquals(PROJECT_ROOT_DIRECTORY + "/.dwplus/status.json", persistence.getStatusFile());
    }

    @Test
    public void getFilename() {
        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        FilePersistence persistence = new FilePersistence(PROJECT_ROOT_DIRECTORY);
        assertEquals(PROJECT_ROOT_DIRECTORY + "/de/tests/TestCase.dbtest", persistence.getFilename(testCase));
        assertEquals(PROJECT_ROOT_DIRECTORY + "/de/tests/TestCase.dbtest", persistence.getFilename(testCase.getIdentifier()));
    }

    @Test
    public void getDirectory() {
        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        FilePersistence persistence = new FilePersistence(PROJECT_ROOT_DIRECTORY);
        assertEquals(PROJECT_ROOT_DIRECTORY + "/de/tests", persistence.getDirectory(testCase));
        assertEquals(PROJECT_ROOT_DIRECTORY + "/de/tests", persistence.getDirectory(testCase.getPackage()));
    }

    @Test
    public void save() throws IOException {
        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        FilePersistence persistence = new FilePersistence(PROJECT_ROOT_DIRECTORY);
        persistence.initialize();
        persistence.save(testCase);
        Path testCasePath = Paths.get(PROJECT_ROOT_DIRECTORY + "/de/tests/TestCase.dbtest");
        assertTrue(Files.exists(testCasePath));
    }

    @Test
    public void loadFormFile() throws IOException {
        FilePersistence persistence = new FilePersistence(PROJECT_ROOT_DIRECTORY);
        TestCase testCase = persistence.loadFromFile("/opt/dbtest/de/tests/TestCase.dbtest");
        assertEquals("TestCase", testCase.getName());
        assertEquals("de.tests", testCase.getPackage());
    }

    @Test
    public void loadFromDirecrory() throws IOException {
        FilePersistence persistence = new FilePersistence(PROJECT_ROOT_DIRECTORY);
        Set<TestCase> testCases = persistence.loadFromDirectory("/opt/dbtest/de");
        assertTrue(testCases.size() > 0);
    }

    @Test
    public void load() throws IOException {
        FilePersistence persistence = new FilePersistence(PROJECT_ROOT_DIRECTORY);
        TestCase testCase = persistence.load("de.tests.TestCase");
        assertEquals("TestCase", testCase.getName());
        assertEquals("de.tests", testCase.getPackage());
    }

    @Test
    public void loadMultiple() throws IOException {
        FilePersistence persistence = new FilePersistence(PROJECT_ROOT_DIRECTORY);
        Set<TestCase> testCases = persistence.loadMultiple("de");
        assertTrue(testCases.size() > 0);
    }

    @Test
    public void loadConfiguration() throws IOException {
        FilePersistence persistence = new FilePersistence(PROJECT_ROOT_DIRECTORY);
        Configuration config = persistence.loadConfiguration();
        assertNotNull(config);
    }

    @Test
    public void saveConfiguration() throws IOException {
        Configuration config = new Configuration();

        config.addTarget(TargetDummies.getDefaultTarget());

        FilePersistence persistence = new FilePersistence(PROJECT_ROOT_DIRECTORY);
        persistence.saveConfiguration(config);
    }

    @Test
    public void loadStatusItems() throws IOException {
        FilePersistence persistence = new FilePersistence(PROJECT_ROOT_DIRECTORY);
        Set<StatusItem> statusItemSet = persistence.loadStatus();
        assertNotNull(statusItemSet);
        assertTrue(statusItemSet.size() > 0);
    }

    @Test
    public void loadStatusItemsForTestCase() throws IOException {
        FilePersistence persistence = new FilePersistence(PROJECT_ROOT_DIRECTORY);
        Set<StatusItem> statusItemSet = persistence.loadStatus("de.tests.TestCase");
        assertNotNull(statusItemSet);
        assertTrue(statusItemSet.size() > 0);
    }

    @Test
    public void saveStatusItems() throws IOException {
        FilePersistence persistence = new FilePersistence(PROJECT_ROOT_DIRECTORY);

        TestCase testCase = new TestCase(TestCaseDummies.TEST_CASE_BODY_PASSED);
        testCase.setStatus(TargetDummies.getDefaultTarget().getIdentifier(), TestCase.TCS_PASSED);

        persistence.saveStatus(testCase);
    }

}