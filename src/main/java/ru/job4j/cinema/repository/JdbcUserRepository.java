package ru.job4j.cinema.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация хранилища пользователей
 * @see ru.job4j.cinema.model.User
 * @author Alexander Emelyanov
 * @version 1.0
 */
@Slf4j
@AllArgsConstructor
@Repository
public class JdbcUserRepository implements UserRepository {

    /**
     * SQL запрос по выбору всех пользователей из таблицы users
     */
    private static final String FIND_ALL_SELECT = """
            SELECT
                id,
                username,
                email,
                phone,
                password
            FROM users
            """;

    /**
     * SQL запрос по выбору всех пользователей из таблицы users с фильтром по id
     */
    private static final String FIND_BY_ID_SELECT = FIND_ALL_SELECT + """
            WHERE id = ?
            """;

    /**
     * SQL запрос по выбору всех пользователей из таблицы users с фильтром по email
     */
    private static final String FIND_BY_EMAIL_SELECT = FIND_ALL_SELECT + """
            WHERE email = ?
            """;

    /**
     * SQL запрос по выбору всех пользователей из таблицы users с фильтром по phone
     */
    private static final String FIND_BY_PHONE_SELECT = FIND_ALL_SELECT + """
            WHERE phone = ?
            """;

    /**
     * SQL запрос по добавлению строк в таблицу users
     */
    private static final String INSERT_INTO = """
            INSERT INTO users(username, email, phone,
            password) VALUES (?, ?, ?, ?)
            """;

    /**
     * SQL запрос по обновлению данных пользователя в таблице users
     */
    private static final String UPDATE = """
            UPDATE users SET username = ?, email = ?, phone = ?,
            password = ? WHERE id = ?
            """;

    /**
     * SQL запрос по удалению пользователей из таблицы users с фильтром по id
     */
    private static final String DELETE = """
            DELETE FROM users WHERE id = ?
            """;

    /**
     * Объект для выполнения подключения к базе данных приложения
     */
    private final DataSource dataSource;

    /**
     * Возвращает список всех пользователей.
     *
     * @return список всех пользователей
     */
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(FIND_ALL_SELECT)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    users.add(getUserFromResultSet(it));
                }
            }
        } catch (Exception e) {
            log.info("Исключение в методе findAll() класса JdbcUserRepository ", e);
        }
        return users;
    }

    /**
     * Выполняет поиск пользователя по идентификатору. При успешном нахождении возвращает
     * Optional с объектом пользователя. Иначе возвращает Optional.empty().
     *
     * @param id идентификатор пользователя
     * @return Optional.of(user) при успешном нахождении, иначе Optional.empty()
     */
    @Override
    public Optional<User> findById(int id) {
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(FIND_BY_ID_SELECT)
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return Optional.of(getUserFromResultSet(it));
                }
            }
        } catch (Exception e) {
            log.info("Исключение в методе findById() класса JdbcUserRepository ", e);
        }
        return Optional.empty();
    }

    /**
     * Выполняет поиск пользователя по наименованию почтового адреса. При успешном нахождении
     * возвращает Optional с объектом пользователя. Иначе возвращает Optional.empty().
     *
     * @param email почтовый адрес пользователя
     * @return Optional.of(user) при успешном нахождении, иначе Optional.empty()
     */
    @Override
    public Optional<User> findUserByEmail(String email) {
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(FIND_BY_EMAIL_SELECT)
        ) {
            ps.setString(1, email);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return Optional.of(getUserFromResultSet(it));
                }
            }
        } catch (Exception e) {
            log.info("Исключение в методе findByEmail() класса JdbcUserRepository ", e);
        }
        return Optional.empty();
    }

    /**
     * Выполняет поиск пользователя по номеру телефона. При успешном нахождении
     * возвращает Optional с объектом пользователя. Иначе возвращает Optional.empty().
     *
     * @param phone номер телефона пользователя
     * @return Optional.of(user) при успешном нахождении, иначе Optional.empty()
     */
    @Override
    public Optional<User> findUserByPhone(String phone) {
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(FIND_BY_PHONE_SELECT)
        ) {
            ps.setString(1, phone);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return Optional.of(getUserFromResultSet(it));
                }
            }
        } catch (Exception e) {
            log.info("Исключение в методе findByPhone() класса JdbcUserRepository ", e);
        }
        return Optional.empty();
    }

    /**
     * Выполняет сохранение пользователя. При успешном сохранении возвращает Optional с
     * объектом пользователя, у которого проинициализировано id. Иначе возвращает Optional.empty()
     * Сохранение не произойдет, если email или номер телефона использовались при регистрации
     * другим пользователем.
     *
     * @param user сохраняемый пользователь
     * @return Optional.of(user) при успешном сохранении, иначе Optional.empty()
     */
    @Override
    public Optional<User> save(User user) {
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(INSERT_INTO,
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPassword());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                    return Optional.of(user);
                }
            }
        } catch (Exception e) {
            log.info("Исключение в методе save() класса JdbcUserRepository ", e);
        }
        return Optional.empty();
    }

    /**
     * Выполняет обновление пользователя.
     *
     * @param user объект пользователя
     * @return true при успешном обновлении пользователя, иначе false
     */
    @Override
    public boolean update(User user) {
        boolean result = false;
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(UPDATE)
        ) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPassword());
            ps.setInt(5, user.getId());
            result = ps.executeUpdate() > 0;

        } catch (Exception e) {
            log.info("Исключение в методе update() класса JdbcUserRepository ", e);
        }
        return result;
    }

    /**
     * Выполняет удаление пользователя по идентификатору. При успешном
     * удалении возвращает true, при неудачном false.
     *
     * @param id идентификатор пользователя
     * @return true при успешном удалении пользователя, иначе false
     */
    @Override
    public boolean deleteById(int id) {
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(DELETE)
        ) {
            ps.setInt(1, id);
            int ids = ps.executeUpdate();
            if (ids > 0) {
                return true;
            }
        } catch (Exception e) {
            log.info("Исключение в методе deleteById() класса JdbcUserRepository ", e);
        }
        return false;
    }

    /**
     * Вспомогательный метод выполняет создание
     * объекта User из объекта ResultSet.
     *
     * @param it ResultSet SQL запроса к базе данных
     * @return объект User
     */
    private static User getUserFromResultSet(ResultSet it) throws SQLException {
        return new User(it.getInt("id"), it.getString("username"),
                it.getString("email"), it.getString("phone"), it.getString("password"));
    }
}
