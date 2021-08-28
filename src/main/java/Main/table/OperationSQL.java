package Main.table;

import Main.config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class OperationSQL {
    //TODO refactoring - make one method
    private static Connection getConnection() throws SQLException {
        Connection con = DriverManager.getConnection(
                Config.getProperty(Config.DB_URL),
                Config.getProperty(Config.DB_LOGIN),
                Config.getProperty(Config.DB_PASSWORD)
        );
        return con;
    }
}
