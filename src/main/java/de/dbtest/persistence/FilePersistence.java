package de.dbtest.persistence;

import com.sun.org.apache.bcel.internal.generic.LOR;
import de.dbtest.common.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The file persisince class is used to manage test cases on file systems.
 *
 * @author Lennard Scheffler
 */
public class FilePersistence {

    /**
     * Default file extension for test cases.
     */
    private static final String TEST_FILE_EXTENSION = ".dbtest";


    /* Private POJO attributes ****************************************************************************************/

    private static final Logger LOGGER = LogManager.getLogger(FilePersistence.class);

    private String rootDirectory;


    /**
     * Default constructor for the file persistence class.
     *
     * @param rootDirectory Path of the root directory which should include the package folders and test cases.
     */
    public FilePersistence(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    /**
     * Translate a full qualified test case name (FQTN) into an absolute path based on the project root directory. The
     * filename is composed out of the project root directory, the package transleted into single sub folders and the
     * test case name including the defaut test case file extension.<br />
     * For example: If the root directory is "/home/user01/TestProject" and the FQTN is "de.tests.TestCase",
     * the returned filename is "/home/user01/TestProject/de/tests/TestCase.dbtest".
     *
     * @param identifier Full qualified test case name.
     * @return Absolute path to the test case based on project root, package name and test case name.
     */
    public String getFilename(String identifier) {
        String[] identifierSplit = identifier.split("\\.");
        String filename = rootDirectory + "/" + String.join("/", identifierSplit) + TEST_FILE_EXTENSION;

        LOGGER.info("Detmine filename from identifier: {} -> {}", identifier, filename);

        return filename;
    }

    /**
     * See explanation of invoked method.
     *
     * @param testCase Test case object used for dertermining the file name.
     * @return Absolute path to the test case based on project root, package name and test case name.
     */
    public String getFilename(TestCase testCase) {
        return getFilename(testCase.getIdentifier());
    }

    /**
     * Translate a full qualified package name (FQPN) into an absolute path based on the project root directory. The
     * directory is composed out of the project root directory and the package transleted into single sub folders<br />
     * For example: If the root directory is "/home/user01/TestProject" and the FQPN is "de.tests",
     * the returned filename is "/home/user01/TestProject/de/tests".
     *
     * @param pack Full qualified package name.
     * @return Absolute path to the package based on project root and package name.
     */
    public String getDirectory(String pack) {
        String[] packageSplit = pack.split("\\.");
        String directory = rootDirectory + "/" + String.join("/", packageSplit);

        LOGGER.info("Determine directory from package definition: {} -> {}", pack, directory);

        return directory;
    }

    /**
     * See explanation of invoked method.
     *
     * @param testCase Test Case to get the directory from.
     * @return Absolute path to the package based on project root and package name.
     */
    public String getDirectory(TestCase testCase) {
        return getDirectory(testCase.getPackage());
    }



    public void save(TestCase testCase) throws IOException {

        Path testCaseDirectoryPath = Paths.get(getDirectory(testCase));
        Path testCasePath = Paths.get(getFilename(testCase));

        LOGGER.info("Create directories, if neccessary.");
        Files.createDirectories(testCaseDirectoryPath);

        saveStatus(testCase);

        LOGGER.info("Write Test Case to file {}.", testCasePath.toAbsolutePath());
        Files.write(testCasePath, testCase.getBody().getBytes());
    }

    /**
     * Load a test case from a file without any respect to the project root directory.
     *
     * @param filename Full path to the file.
     * @return Test case object loaded from the given file.
     * @throws IOException
     */
    public TestCase loadFromFile(String filename) throws IOException {

        LOGGER.info("Read Test Case from file {}", filename);
        Path testCasePath = Paths.get(filename);
        String body = new String(Files.readAllBytes(testCasePath));

        TestCase testCase = new TestCase(body);

        Set<StatusItem> statusItems = loadStatus(testCase.getIdentifier());
        testCase.setStatus(statusItems);

        return testCase;
    }

    /**
     * Load all test cases placed in a directory and the available sub directories without any respect to the project
     * root directory.
     *
     * @param directory Full path to the directory.
     * @return A set of test cases that habe been found in the base directory.
     * @throws IOException
     */
    public Set<TestCase> loadFromDirectory(String directory) throws IOException {
        Set<TestCase> testCases = new HashSet<TestCase>();

        DirectoryStream<Path> paths = Files.newDirectoryStream(Paths.get(directory));
        for (Path p : paths) {
            if (Files.isDirectory(p)) {
                testCases.addAll(loadFromDirectory(p.toString()));
            }
            else if (p.toString().endsWith(TEST_FILE_EXTENSION)) {
                testCases.add(loadFromFile(p.toString()));
            }
        }

        return testCases;
    }

    /**
     * Load a test case form a file. The filename is determinded be the project root directory and the full qualified
     * test case identifier (FQTN). (See also FilePersistence.laodFormFile(...))
     *
     * @param identifier Full qualified test case identifer.
     * @return Test case object loaded from the file.
     * @throws IOException
     */
    public TestCase load(String identifier) throws IOException {
        return loadFromFile(getFilename(identifier));
    }

    /**
     * Load a set of test cases based on package information. All testcases within a package (and available sub
     * packages) will be loaded. (See also FilePersistence.loadFromDirectory).
     *
     * @param identifier Full qualified package name (FQPN).
     * @return A set of test cases that habe been found in the package.
     * @throws IOException
     */
    public Set<TestCase> loadMultiple(String identifier) throws IOException {
        String[] identifierSplit = identifier.split("\\.");
        String baseDirectory = rootDirectory + "/" + String.join("/", identifierSplit);

        return loadFromDirectory(baseDirectory);
    }

    /**
     * Initialize a directory for managing test cases with DWPlus. The basic files and directories will be created.
     *
     * @throws IOException
     */
    public void initialize() throws IOException {
        LOGGER.info("Inizializing root directory {}", rootDirectory);

        Path rootDirectoryPath = Paths.get(rootDirectory);
        if (!Files.exists(rootDirectoryPath)) {
            LOGGER.info("Creating root directory.");
            Files.createDirectory(rootDirectoryPath);
        }

        Path configDirectoryPath = Paths.get(getConfigDirectory());
        if (!Files.exists(configDirectoryPath)) {
            LOGGER.info("Createing configuration directory.");
            Files.createDirectory(configDirectoryPath);
        }
    }

    /**
     * Get the config directory with repect to the project root directory.
     *
     * @return Absolute path to the configuration directory within the project root directory.
     */
    public String getConfigDirectory() {
        return rootDirectory + "/.dbtest";
    }

    /**
     * Get the configuration file with respect to the project root directry.
     * @return Absolute path incl. filename to the configuraiton file with repect to the project root directory.
     */
    public String getConfigFile() {
        return rootDirectory + "/dbtest.json";
    }

    /**
     * Get the status file containing all available statuses to the managed test cases.
     *
     * @return Filename to open the status file.
     */
    public String getStatusFile() {
        return getConfigDirectory() + "/status.json";
    }

    /**
     * Load the configuration form the project root directory. The configruation is usually placed in the configuration
     * file placed in the project root directory.
     *
     * @return Configruation object.
     * @throws IOException
     */
    public Configuration loadConfiguration() throws IOException {
        String configFile = getConfigFile();
        LOGGER.info("Loading configuration from {}", configFile);
        ObjectMapper mapper = new ObjectMapper();
        File infile = new File(configFile);
        return mapper.readValue(infile, Configuration.class);
    }

    /**
     * Save the given configuration object to a JSON file within the project root directory.
     *
     * @param config Configuration object that has to be stored.
     * @throws IOException
     */
    public void saveConfiguration(Configuration config) throws IOException {
        String configFile = getConfigFile();
        ObjectMapper mapper = new ObjectMapper();
        File outfile = new File(configFile);

        LOGGER.info("Write config file to {}", configFile);
        mapper.writeValue(outfile, config);
    }


    public Set<StatusItem> loadStatus() throws IOException {
        String filename = getStatusFile();
        File statusFile = new File(filename);

        LOGGER.info("Loading file status infomation from {}", filename);

        if (!statusFile.exists()) {
            LOGGER.info("No status file persited, return empty status information.");
            return new HashSet<StatusItem>();
        }

        ObjectMapper mapper = new ObjectMapper();
        List<StatusItem> statusItems = Arrays.asList(mapper.readValue(statusFile, StatusItem[].class));
        return new HashSet<StatusItem>(statusItems);
    }

    public Set<StatusItem> loadStatus(String testCaseIdentifier) throws IOException {
        Set<StatusItem> statusItemSet = loadStatus();
        LOGGER.info("Determining status for Test Case {}", testCaseIdentifier);
        return statusItemSet.stream().filter(s -> s.getTestCaseIdentifier().equals(testCaseIdentifier)).collect(Collectors.toSet());
    }

    public void saveStatus(TestCase testCase) throws IOException {
        String filename = getStatusFile();
        File statusFile = new File(filename);
        ObjectMapper mapper = new ObjectMapper();
        Set<StatusItem> statusItems = loadStatus();

        LOGGER.info("Adding Status information from Test Case {} to status information.", testCase.getIdentifier());
        statusItems.addAll(testCase.getStatusSet());

        LOGGER.info("Writing status to file {}", filename);
        mapper.writeValue(statusFile, statusItems);

    }



}
