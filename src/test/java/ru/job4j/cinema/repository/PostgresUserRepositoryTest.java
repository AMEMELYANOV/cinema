package ru.job4j.cinema.repository;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.config.DataSourceConfig;
import ru.job4j.cinema.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PostgresUserRepositoryTest {

    /**
     * SQL запрос по очистке от данных таблицы users
     */
    private static final String CLEAR_TABLE = """
            DELETE FROM users
            """;

    /**
     * Объект репозитория PostgresUserRepository
     */
    private PostgresUserRepository userRepository;

    /**
     * Пользователь
     */
    private User user;

    /**
     * Создает необходимые для выполнения тестов общие объекты.
     * Создание выполняется перед каждым тестом.
     */
    @BeforeEach
    public void setup() {
        userRepository = new PostgresUserRepository(
                new DataSourceConfig().loadPool());
        user = User.builder()
                .id(1)
                .username("username")
                .email("email")
                .phone("phone")
                .password("pass")
                .build();
        userRepository.save(user);
    }

    /**
     * Очистка таблицы users, выполняется после каждого теста.
     *
     * @throws SQLException если происходит ошибка доступа к базе данных
     */
    @AfterEach
    public void wipeTable() throws SQLException {
        try (BasicDataSource pool = new DataSourceConfig().loadPool();
             Connection connection = pool.getConnection();
             PreparedStatement statement = connection.prepareStatement(CLEAR_TABLE)
        ) {
            statement.execute();
        }
    }

    /**
     * Создается объект user и сохраняется в базе данных.
     * По полю id объект user находится в базе данных, сохраняется в объект userFromDB
     * при помощи метода {@link PostgresUserRepository#findById(int)}
     * и проверяется его эквивалентность объекту user по полю name.
     */
    @Test
    void whenSaveUserThenGetTheSameFromDatabase() {
        User userFromDB = userRepository.findById(user.getId()).get();

        assertThat(user.getUsername()).isEqualTo(userFromDB.getUsername());
    }

    /**
     * Создается объект user и сохраняется в базе данных.
     * Выполняется изменение данных с обновлением объекта user в
     * базе данных при помощи метода {@link PostgresUserRepository#update(User)}.
     * Результат обновления записывается в переменную updateResult.
     * По полю id объект user находится в базе данных, сохраняется в объект userFromDB
     * при помощи метода {@link PostgresUserRepository#findById(int)},
     * далее проверяется его эквивалентность объекту user
     * и переменной updateResult на эквивалентность true.
     */
    @Test
    public void whenUpdateUserThenGetTheSameFromDatabase() {
        user.setUsername("Name2");
        boolean updateResult = userRepository.update(user);

        User userFromDb = userRepository.findById(user.getId()).get();
        assertThat(userFromDb).isEqualTo(user);
        assertThat(updateResult).isEqualTo(true);
    }

    /**
     * Создается объект user и сохраняется в базе данных, поле id записывается в переменную oldId.
     * У объекта user выполняется изменение полей username и id, далее производится обновление
     * объекта user в базе данных при помощи метода {@link PostgresUserRepository#update(User)}.
     * Результат обновления записывается в переменную updateResult.
     * По переменной oldId объект user находится в базе данных, сохраняется в объект userFromDB
     * при помощи метода {@link PostgresUserRepository#findById(int)}, далее
     * проверяется отсутствие эквивалентности объекту user
     * и переменной updateResult на эквивалентность false.
     */
    @Test
    public void whenUpdateUserThenFalse() {
        int oldId = user.getId();
        user.setUsername("Name2");
        user.setId(user.getId() + 1);
        boolean updateResult = userRepository.update(user);

        User userFromDb = userRepository.findById(oldId).get();
        assertThat(userFromDb).isNotEqualTo(user);
        assertThat(updateResult).isEqualTo(false);
    }

    /**
     * Создаются объекты user, user2 и сохраняются в базе данных.
     * По полю id объект user находится в базе данных при помощи метода
     * {@link PostgresUserRepository#findById(int)} и проверяется на эквивалентность
     * объекту user по полю name.
     */
    @Test
    public void whenFindUserByIdThenGetUserFromDatabase() {
        User user2 = User.builder()
                .username("Name2")
                .email("mail2@mail.com")
                .phone("1232")
                .password("password")
                .build();
        userRepository.save(user2);

        User userFromDB = userRepository.findById(user.getId()).get();
        assertThat(userFromDB.getUsername()).isEqualTo(user.getUsername());
    }

    /**
     * Создается объект user и сохраняется в базе данных.
     * По полю id объект user находится в базе данных при помощи
     * метода {@link PostgresUserRepository#findById(int)} и
     * проверяется на эквивалентность Optional.empty().
     */
    @Test
    public void whenFindUserByIdThenDoNotGetUserFromDatabase() {
        assertThat(userRepository.findById(user.getId() + 1)).isEqualTo(Optional.empty());
    }

    /**
     * Создается объект user и сохраняется в базе данных.
     * По полю id объект user удаляется из базы данных при помощи метода
     * {@link PostgresUserRepository#deleteById(int)}
     * Метод {@link PostgresUserRepository#deleteById(int)} при удалении
     * объекта возвращает true, вызов метода {@link PostgresUserRepository#findById(int)}
     * проверяется на эквивалентность Optional.empty().
     */
    @Test
    public void whenDeleteUserByIdIsTrueAndThenDoNotGetUserFromDatabase() {
        int id = user.getId();

        assertThat(userRepository.deleteById(id)).isEqualTo(true);
        assertThat(userRepository.findById(id)).isEqualTo(Optional.empty());
    }

    /**
     * Создаются объекты user, user2 и сохраняются в базе данных.
     * Через вызов метода {@link PostgresUserRepository#findAll()}
     * получаем список объекты users, который сортируется по id.
     * Выполняем проверку размера списка и содержание элементов
     * на эквивалентность объектам user и user2 по полям name.
     */
    @Test
    public void whenFindAllUsersThenGetListOfAllUsers() {
        User user2 = User.builder()
                .username("Name2")
                .email("mail2@mail.com")
                .phone("1232")
                .password("password2")
                .build();
        userRepository.save(user2);
        List<User> users = userRepository.findAll();
        users.sort(Comparator.comparing(User::getId));

        assertThat(users.size()).isEqualTo(2);
        assertThat(users.get(0)).isEqualTo(user);
        assertThat(users.get(1)).isEqualTo(user2);
    }
}