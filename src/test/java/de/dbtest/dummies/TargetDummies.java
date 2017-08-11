package de.dbtest.dummies;

import de.dbtest.common.UniversalDatabaseConnector;

public class TargetDummies {

    private static final String HOST = "localhost";
    private static final int PORT = 3306;
    private static final String DATABASE = "information_schema";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static UniversalDatabaseConnector getDefaultTarget() {
        return new UniversalDatabaseConnector(
                UniversalDatabaseConnector.DBT_MYSQL,
                HOST,
                PORT,
                DATABASE,
                USER,
                PASSWORD
        );
    }

}
