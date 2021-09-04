package main.ServiceSQL;

import Main.config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class OperationSQL {
    protected static final String urlSQL = "jdbc:postgresql://localhost:5432/telegram_bot";
    protected static final String loginSQL = "postgres";
    protected static final String passwordSQL = "596228";

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
