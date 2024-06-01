package jm.task.core.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

public class UserDaoJDBCImpl implements UserDao {
    private final Connection connection;

    public UserDaoJDBCImpl() {
        this.connection = Util.connect();
    }

    public void createUsersTable() {
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                "CREATE TABLE IF NOT EXISTS users(" +
                    "id BIGSERIAL PRIMARY KEY," +
                    "name VARCHAR(255)," +
                    "lastname VARCHAR(255)," +
                    "age SMALLINT);"
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void dropUsersTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS  users");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveUser(String name, String lastName, byte age) {

        try (PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO users(name, lastname, age) VALUES (?,?,?)")) {
            statement.setString(1, name);
            statement.setString(2, lastName);
            statement.setByte(3, age);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeUserById(long id) {
        try (PreparedStatement statement = connection.prepareStatement(
            "DELETE FROM users WHERE id=?")) {
            statement.setLong(1, id);

            if (statement.executeUpdate() > 0) {
                System.out.println("Удалён пользователь с ID - " + id);
            } else {
                System.out.println("Нет пользователя с ID - " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT * FROM users")) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");
                    String lastName = resultSet.getString("lastname");
                    byte age = resultSet.getByte("age");

                    User user = new User(id, name, lastName, age);
                    allUsers.add(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return allUsers;
    }

    public void cleanUsersTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
