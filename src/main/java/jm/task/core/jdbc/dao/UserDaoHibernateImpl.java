package jm.task.core.jdbc.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

public class UserDaoHibernateImpl implements UserDao {
    private final SessionFactory factory;

    public UserDaoHibernateImpl() {
        this.factory = Util.setUp();
    }


    @Override
    public void createUsersTable() {
        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();
            NativeQuery<?> query = session.createSQLQuery(
                "CREATE TABLE IF NOT EXISTS users(" +
                    "id BIGSERIAL PRIMARY KEY," +
                    "name VARCHAR(512)," +
                    "lastname VARCHAR(512)," +
                    "age SMALLINT)");
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Не удалось создать таблицу users", e);
        }
    }

    @Override
    public void dropUsersTable() {
        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();
            NativeQuery<?> query = session.createNativeQuery(
                "DROP TABLE IF EXISTS users"
            );
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Не удалось удалить таблицу users", e);
        }

    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        User user = new User(name, lastName, age);

        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();

            session.save(user);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Не удалось добавить пользователя с именем - " + name, e);
        }
    }

    @Override
    public void removeUserById(long id) {

        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();

            User user = Optional.ofNullable(session.get(User.class, id))
                .orElseThrow(() -> new RuntimeException("Нет пользователя с ID - " + id));

            session.delete(user);

            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении пользователя", e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();

            List<User> allUsers = new ArrayList<>(session.createQuery("FROM User").getResultList());

            transaction.commit();

            return allUsers;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении пользователей", e);
        }
    }

    @Override
    public void cleanUsersTable() {
        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();

            NativeQuery<?> query = session.createNativeQuery(
                "TRUNCATE TABLE users RESTART IDENTITY CASCADE"
            );
            query.executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при очистке таблицы пользователей", e);
        }
    }
}
