package jm.task.core.jdbc.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import jm.task.core.jdbc.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class Util {

    public static Connection connect() {
        Properties properties = getProperties();

        try {
            return DriverManager.getConnection(
                properties.getProperty("host"),
                properties.getProperty("user"),
                properties.getProperty("password"));
        } catch (SQLException e) {
            throw new RuntimeException("Вы не смогли подключится к БД проверьте настройки");
        }
    }

    public static SessionFactory setUp() {
        Properties properties = getProperties();

        StandardServiceRegistry registry =
            new StandardServiceRegistryBuilder()
            .applySetting("hibernate.connection.url", properties.getProperty("host"))
            .applySetting("hibernate.connection.username", properties.getProperty("user"))
            .applySetting("hibernate.connection.password", properties.getProperty("password"))
            .applySetting("hibernate.show_sql", properties.getProperty("hibernate.show_sql"))
//            .applySetting("hibernate.format_sql", properties.getProperty("hibernate.format_sql"))
//            .applySetting("hibernate.use_sql_comments", properties.getProperty("hibernate.use_sql_comments"))
//            .applySetting("hibernate.hbm2ddl.auto", properties.getProperty("hibernate.hbm2ddl.auto"))
                .build();
        try {
            Metadata metadata = new MetadataSources(registry)
                .addAnnotatedClass(User.class)
                .buildMetadata();

            return metadata.getSessionFactoryBuilder().build();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw new RuntimeException("Вы не смогли подключится к БД проверьте настройки");
        }
    }

    private static Properties getProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = Util.class.getClassLoader()
            .getResourceAsStream("application.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
