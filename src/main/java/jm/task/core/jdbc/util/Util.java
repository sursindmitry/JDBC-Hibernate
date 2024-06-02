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
        loadDriverClass();

        try {
            return DriverManager.getConnection(
                properties.getProperty("host"),
                properties.getProperty("user"),
                properties.getProperty("password")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Вы не смогли подключится к БД проверьте настройки");
        }
    }

    public static SessionFactory setUp() {
        Properties properties = getProperties();

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .applySetting("hibernate.connection.url", properties.getProperty("hibernate.connection.url"))
            .applySetting("hibernate.connection.username", properties.getProperty("hibernate.connection.username"))
            .applySetting("hibernate.connection.password", properties.getProperty("hibernate.connection.password"))
            .applySetting("hibernate.show_sql", properties.getProperty("hibernate.show_sql"))
            .build();
        try {
            MetadataSources sources = new MetadataSources(registry);
            sources.addAnnotatedClass(User.class);

            Metadata metadata = sources.getMetadataBuilder().build();

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

    private static void loadDriverClass() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
