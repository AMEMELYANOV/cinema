package ru.job4j.cinema.repository;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.config.DataSourceConfig;
import ru.job4j.cinema.model.Show;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тест класс реализации хранилища сеансов
 *
 * @author Alexander Emelyanov
 * @version 1.0
 * @see JdbcShowRepository
 */
class JdbcShowRepositoryTest {

    /**
     * SQL запрос по очистке от данных таблицы shows
     */
    private static final String CLEAR_TABLE = """
            DELETE FROM shows
            """;

    /**
     * Объект репозитория JdbcShowRepository
     */
    private JdbcShowRepository showRepository;

    /**
     * Сеанс
     */
    private Show show;

    /**
     * Создает необходимые для выполнения тестов общие объекты.
     * Создание выполняется перед каждым тестом.
     */
    @BeforeEach
    public void setup() {
        showRepository = new JdbcShowRepository(
                new DataSourceConfig().loadPool());
        show = Show.builder()
                .id(1)
                .name("name")
                .description("description")
                .build();
        showRepository.save(show);
    }

    /**
     * Очистка таблицы shows, выполняется после каждого теста.
     *
     * @throws SQLException если происходит ошибка доступа к базе данных
     */
    @AfterEach
    public void wipeTable() throws SQLException {
        try (BasicDataSource dataSource = new DataSourceConfig().loadPool();
             Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CLEAR_TABLE)
        ) {
            statement.execute();
        }
    }

    /**
     * Создается объект show и сохраняется в базе данных.
     * По полю id объект show в базе данных, сохраняется в объект showFromDB
     * при помощи метода {@link JdbcShowRepository#findById(int)}
     * и проверяется его эквивалентность объекту show по полю name.
     */
    @Test
    void whenSaveShowThenGetTheSameFromDatabase() {
        Show showFromDB = showRepository.findById(show.getId()).get();

        assertThat(show.getName()).isEqualTo(showFromDB.getName());
    }

    /**
     * Создается объект show и сохраняется в базе данных.
     * Выполняется изменение данных с обновлением объекта show в
     * базе данных при помощи метода {@link JdbcShowRepository#update(Show)}.
     * Результат обновления записывается в переменную updateResult.
     * По полю id объект show находится в базе данных, сохраняется в объект showFromDB
     * при помощи метода {@link JdbcShowRepository#findById(int)},
     * далее проверяется его эквивалентность объекту show
     * и переменной updateResult на эквивалентность true.
     */
    @Test
    public void whenUpdateShowThenGetTheSameFromDatabase() {
        show.setName("Name2");
        boolean updateResult = showRepository.update(show);

        Show showFromDb = showRepository.findById(show.getId()).get();
        assertThat(showFromDb).isEqualTo(show);
        assertThat(updateResult).isEqualTo(true);
    }

    /**
     * Создается объект show и сохраняется в базе данных, поле id записывается в переменную oldId.
     * У объекта show выполняется изменение полей username и id, далее производится обновление
     * объекта show в базе данных при помощи метода {@link JdbcShowRepository#update(Show)}.
     * Результат обновления записывается в переменную updateResult.
     * По переменной oldId объект show находится в базе данных, сохраняется в объект showFromDB
     * при помощи метода {@link JdbcShowRepository#findById(int)}, далее
     * проверяется отсутствие эквивалентности объекту show
     * и переменной updateResult на эквивалентность false.
     */
    @Test
    public void whenUpdateUserThenFalse() {
        int oldId = show.getId();
        show.setName("Name2");
        show.setId(show.getId() + 1);
        boolean updateResult = showRepository.update(show);

        Show showFromDb = showRepository.findById(oldId).get();
        assertThat(showFromDb).isNotEqualTo(show);
        assertThat(updateResult).isEqualTo(false);
    }

    /**
     * Создаются объекты show, show2 и сохраняются в базе данных.
     * По полю id объект show находится в базе данных при помощи метода
     * {@link JdbcShowRepository#findById(int)} и проверяется на эквивалентность
     * объекту show по полю name.
     */
    @Test
    public void whenFindShowByIdThenGetShowFromDatabase() {
        Show show2 = Show.builder()
                .name("Name2")
                .description("Description2")
                .build();
        showRepository.save(show2);

        Show showFromDB = showRepository.findById(show.getId()).get();
        assertThat(showFromDB.getName()).isEqualTo(show.getName());
    }

    /**
     * Создается объект show и сохраняется в базе данных.
     * По полю id объект show находится в базе данных при помощи
     * метода {@link JdbcShowRepository#findById(int)} и
     * проверяется на эквивалентность Optional.empty().
     */
    @Test
    public void whenFindShowByIdThenDoNotGetShowFromDatabase() {
        assertThat(showRepository.findById(show.getId() + 1)).isEqualTo(Optional.empty());
    }

    /**
     * Создается объект show и сохраняется в базе данных.
     * По полю id объект show удаляется из базы данных при помощи метода
     * {@link JdbcShowRepository#deleteById(int)}
     * Метод {@link JdbcShowRepository#deleteById(int)} при удалении
     * объекта возвращает true, вызов метода {@link JdbcShowRepository#findById(int)}
     * проверяется на эквивалентность Optional.empty().
     */
    @Test
    public void whenDeleteShowByIdIsTrueAndThenDoNotGetShowFromDatabase() {
        int id = show.getId();

        assertThat(showRepository.deleteById(id)).isEqualTo(true);
        assertThat(showRepository.findById(id)).isEqualTo(Optional.empty());
    }

    /**
     * Создаются объекты show, show2 и сохраняются в базе данных.
     * Через вызов метода {@link JdbcShowRepository#findAll()}
     * получаем список объекты shows, который сортируется по id.
     * Выполняем проверку размера списка и содержание элементов
     * на эквивалентность объектам show и show2 по полям name.
     */
    @Test
    public void whenFindAllShowsThenGetListOfAllShows() {
        Show show2 = Show.builder()
                .name("Name2")
                .description("Description2")
                .build();
        showRepository.save(show2);
        List<Show> shows = showRepository.findAll();
        shows.sort(Comparator.comparing(Show::getId));

        assertThat(shows.size()).isEqualTo(2);
        assertThat(shows.get(0).getName()).isEqualTo(show.getName());
        assertThat(shows.get(1).getName()).isEqualTo(show2.getName());
    }
}