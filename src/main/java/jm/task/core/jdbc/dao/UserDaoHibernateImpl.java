package jm.task.core.jdbc.dao;

import java.util.List;
import java.util.Optional;
import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

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
            NativeQuery<?> query = session.createNativeQuery(
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
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();

            NativeQuery<?> query = session.createNativeQuery(
                "DROP TABLE IF EXISTS users"
            );
            query.executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось удалить таблицу users", e);
        }

    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        User user = new User(name, lastName, age);

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            session.save(user);

            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось добавить пользователя с именем - " + name, e);
        }
    }

    @Override
    public void removeUserById(long id) {

        try (Session session = factory.openSession()) {
            Transaction transaction =  session.beginTransaction();

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

        try (Session session = factory.openSession()) {

            return session.createQuery("FROM User", User.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении пользователей", e);
        }
    }

    @Override
    public void cleanUsersTable() {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Query query = session.createQuery("DELETE FROM User");
            query.executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при очистке таблицы пользователей", e);
        }
    }
}
