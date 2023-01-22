package ru.job4j.cinema.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.job4j.cinema.model.Show;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация хранилища сеансов
 * @see ru.job4j.cinema.repository.ShowRepository
 * @author Alexander Emelyanov
 * @version 1.0
 */
@Slf4j
@AllArgsConstructor
@Repository
public class JdbcShowRepository implements ShowRepository {

    /**
     * SQL запрос по выбору всех сеансов из таблицы shows
     */
    private static final String FIND_ALL_SELECT = """
            SELECT
                id,
                name,
                description,
                posterName
            FROM shows
            """;

    /**
     * SQL запрос по выбору сеанса из таблицы shows с фильтром по id
     */
    private static final String FIND_BY_ID_SELECT = FIND_ALL_SELECT + """
            WHERE id = ?
            """;

    /**
     * SQL запрос по добавлению строк в таблицу shows
     */
    private static final String INSERT_INTO = """
            INSERT INTO shows(name, description, posterName) VALUES (?, ?, ?)
            """;

    /**
     * SQL запрос по обновлению данных сеанса в таблице shows
     */
    private static final String UPDATE = """
            UPDATE shows SET name = ?, description = ?, posterName = ?
            WHERE id = ?
            """;

    /**
     * SQL запрос по удалению сеансов из таблицы shows с фильтром по id
     */
    private static final String DELETE = """
            DELETE FROM shows WHERE id = ?
            """;

    /**
     * Объект для выполнения подключения к базе данных приложения
     */
    private final DataSource dataSource;

    /**
     * Возвращает список всех сеансов
     *
     * @return список всех сеансов
     */
    @Override
    public List<Show> findAll() {
        List<Show> shows = new ArrayList<>();
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(FIND_ALL_SELECT)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    shows.add(getUserFromResultSet(it));
                }
            }
        } catch (Exception e) {
            log.info("Исключение в методе findAll() класса JdbcShowRepository ", e);
        }
        return shows;
    }

    /**
     * Выполняет поиск сеанса по идентификатору. При успешном нахождении возвращает
     * Optional с объектом сеанса. Иначе возвращает Optional.empty().
     *
     * @param id идентификатор сеанса
     * @return Optional.of(show) при успешном нахождении, иначе Optional.empty()
     */
    @Override
    public Optional<Show> findById(int id) {
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
            log.info("Исключение в методе findById() класса JdbcShowRepository ", e);
        }
        return Optional.empty();
    }

    /**
     * Выполняет сохранение сеанса. При успешном сохранении возвращает Optional с
     * объектом сеанса, у которого проинициализировано id. Иначе возвращает Optional.empty()
     *
     * @param show сохраняемый сеанс
     * @return Optional.of(shows) при успешном сохранении, иначе Optional.empty()
     */
    @Override
    public Optional<Show> save(Show show) {
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(INSERT_INTO,
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, show.getName());
            ps.setString(2, show.getDescription());
            ps.setString(3, show.getPosterName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    show.setId(id.getInt(1));
                    return Optional.of(show);
                }
            }
        } catch (Exception e) {
            log.info("Исключение в методе save() класса JdbcShowRepository ", e);
        }
        return Optional.empty();
    }

    /**
     * Выполняет обновление объекта сеанс.
     *
     * @param show объект сеанс
     * @return true при успешном обновлении сеанса, иначе false
     */
    @Override
    public boolean update(Show show) {
        boolean result = false;
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(UPDATE)
        ) {
            ps.setString(1, show.getName());
            ps.setString(2, show.getDescription());
            ps.setString(3, show.getPosterName());
            ps.setInt(4, show.getId());
            result = ps.executeUpdate() > 0;
        } catch (Exception e) {
            log.info("Исключение в методе update() класса JdbcShowRepository ", e);
        }
        return result;
    }

    /**
     * Выполняет удаление сеанса по идентификатору. При успешном
     * удалении возвращает true, при неудачном false.
     *
     * @param id идентификатор сеанса
     * @return true при успешном удалении сеанса, иначе false
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
            log.info("Исключение в методе deleteById() класса Show ", e);
        }
        return false;
    }

    /**
     * Вспомогательный метод выполняет создание
     * объекта Show из объекта ResultSet.
     *
     * @param it ResultSet SQL запроса к базе данных
     * @return объект Show
     */
    private static Show getUserFromResultSet(ResultSet it) throws SQLException {
        return new Show(it.getInt("id"), it.getString("name"),
                it.getString("description"), it.getString("posterName"));
    }
}
