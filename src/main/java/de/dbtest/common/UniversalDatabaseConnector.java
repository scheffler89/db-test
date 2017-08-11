package de.dbtest.common;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The universal database connector is used to generate database connection via JDBC to various target systems.
 *
 * @author Lennard Scheffler
 */
public class UniversalDatabaseConnector {

    /* DATABASE DIALECT INDICATORS ************************************************************************************/

    public static final short DBT_MYSQL = 1;
    public static final short DBT_ORACLE = 2;


    /* Private POJO attributes ****************************************************************************************/

    private int databaseType;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    /**
     * Default constructor for UniversalDatabaseConnector class.
     */
    public UniversalDatabaseConnector() {
        this(0, null, 0, null, null, null);
    }

    /**
     * Explicit constructor for UniversalDatabaseConnector class.
     *
     * @param databaseType Database type indicating the JDBC driver used for the connection.
     * @param host Host (FQDN or IP address) where the database is located.
     * @param port Port number of the database.
     * @param database Database or schema name.
     * @param username Username used to the database connection.
     * @param password Password referring to the username.
     */
    public UniversalDatabaseConnector(int databaseType, String host, int port, String database, String username, String password) {
        this.databaseType = databaseType;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public int getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(int databaseType) {
        this.databaseType = databaseType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Connect to a MySQL databae.
     *
     * @return JDBC Connection object if the connection has been established successfilly or null if the connection
     * could not be established.
     */
    public Connection connectMySQL() {

        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/"+ database + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    public Connection connectOracle()  {

        Connection connection = null;

        try {
            Class.forName("oracle.jdbc.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:" + username + "/" + password + "@" + host + ":" + port + ":" + database);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return connection;
    }

    /**
     * Connect to the database with the given connection information. The driver will be determined via the database
     * type parameter.
     *
     * @return JDBC Connection object if the connection has been established successfilly or null if the connection
     * could not be established.
     */
    public Connection connect() {
        if (databaseType == DBT_MYSQL) {
            return connectMySQL();
        }
        if (databaseType == DBT_ORACLE) {
            return connectOracle();
        }

        return null;
    }

    /**
     * Generate an identifying string for the database connector. This string can be used to identify a database
     * connector.
     *
     * @return Identifier String to the specific database connection information.
     */
    @JsonIgnore
    public String getIdentifier() {
        return username + "@" + host + ":" + port + "/" + database;
    }
}
