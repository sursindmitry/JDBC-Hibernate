package jm.task.core.jdbc.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Util {
    public static Connection connect() {
        Properties properties = new Properties();

        try (InputStream inputStream = Util.class.getClassLoader()
            .getResourceAsStream("application.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            try {
                connection = DriverManager.getConnection(
                    properties.getProperty("host"),
                    properties.getProperty("user"),
                    properties.getProperty("password")
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (connection != null) {
            return connection;
        } else {
            throw new RuntimeException("Вы не смогли подключится к БД проверьте настройки");
        }
    }
}
