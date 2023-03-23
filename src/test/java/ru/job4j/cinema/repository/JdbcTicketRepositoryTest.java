package ru.job4j.cinema.repository;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.config.DataSourceConfig;
import ru.job4j.cinema.model.Show;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Тест класс реализации хранилища билетов
 *
 * @author Alexander Emelyanov
 * @version 1.0
 * @see JdbcTicketRepository
 */
class JdbcTicketRepositoryTest {

    /**
     * SQL запрос по очистке от данных таблицы users
     */
    private static final String CLEAR_TABLE_USERS = """
            DELETE FROM users;
            """;

    /**
     * SQL запрос по очистке от данных таблицы shows
     */
    private static final String CLEAR_TABLE_SHOWS = """
            DELETE FROM shows;
            """;

    /**
     * SQL запрос по очистке от данных таблицы tickets
     */
    private static final String CLEAR_TABLE_TICKETS = """
            DELETE FROM tickets;
            """;

    /**
     * Объект репозитория JdbcTicketRepository
     */
    private JdbcTicketRepository ticketRepository;

    /**
     * Объект репозитория JdbcShowRepository
     */
    private JdbcShowRepository showRepository;

    /**
     * Объект репозитория JdbcUserRepository
     */
    private JdbcUserRepository userRepository;

    /**
     * Билет
     */
    private Ticket ticket;

    /**
     * Пользователь
     */
    private User user;

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
        ticketRepository = new JdbcTicketRepository(
                new DataSourceConfig().loadPool());
        showRepository = new JdbcShowRepository(
                new DataSourceConfig().loadPool());
        userRepository = new JdbcUserRepository(
                new DataSourceConfig().loadPool());
        user = User.builder()
                .id(1)
                .username("username")
                .email("email")
                .phone("phone")
                .password("pass")
                .build();
        show = Show.builder()
                .id(1)
                .name("name")
                .description("description")
                .build();
        ticket = Ticket.builder()
                .id(1)
                .show(show)
                .posRow(1)
                .cell(1)
                .user(user)
                .build();
        showRepository.save(show);
        userRepository.save(user);
        ticketRepository.save(ticket);
    }

    /**
     * Очистка таблиц: shows, users, tickets, выполняется после каждого теста.
     *
     * @throws SQLException если происходит ошибка доступа к базе данных
     */
    @AfterEach
    public void wipeTable() throws SQLException {
        try (BasicDataSource dataSource = new DataSourceConfig().loadPool();
             Connection connection = dataSource.getConnection();
             PreparedStatement statement1 = connection.prepareStatement(CLEAR_TABLE_TICKETS);
             PreparedStatement statement2 = connection.prepareStatement(CLEAR_TABLE_SHOWS);
             PreparedStatement statement3 = connection.prepareStatement(CLEAR_TABLE_USERS)
        ) {
            statement1.execute();
            statement2.execute();
            statement3.execute();
        }
    }

    /**
     * Создается объект ticket и сохраняется в базе данных.
     * По полю id объект ticket находится в базе данных, сохраняется в объект showFromDB
     * при помощи метода {@link JdbcTicketRepository#findById(int)}
     * и проверяется его эквивалентность объекту ticket по полю name.
     */
    @Test
    void whenSaveTicketThenGetTheSameFromDatabase() {
        Ticket ticketFromDB = ticketRepository.findById(ticket.getId()).get();

        assertThat(ticketRepository.findById(ticket.getId()).get()).isNotNull()
                .satisfies(oldTicket -> {
                    assertThat(oldTicket.getShow().getId())
                            .isEqualTo(ticketFromDB.getShow().getId());
                    assertThat(oldTicket.getPosRow()).isEqualTo(ticketFromDB.getPosRow());
                    assertThat(oldTicket.getCell()).isEqualTo(ticketFromDB.getCell());
                });
    }

    /**
     * Создается объект ticket и сохраняется в базе данных.
     * Выполняется изменение данных с обновлением объекта ticket в
     * базе данных при помощи метода {@link JdbcTicketRepository#update(Ticket)}.
     * Результат обновления записывается в переменную updateResult.
     * По полю id объект ticket находится в базе данных, сохраняется в объект ticket
     * при помощи метода {@link JdbcTicketRepository#findById(int)},
     * далее проверяется его эквивалентность объекту ticket
     * и переменной updateResult на эквивалентность true.
     */
    @Test
    public void whenUpdateTicketThenGetTheSameFromDatabase() {
        ticket.setPosRow(2);
        boolean updateResult = ticketRepository.update(ticket);
        Ticket ticketFromDB = ticketRepository.findById(ticket.getId()).get();

        assertThat(ticket).isEqualTo(ticketFromDB);
        assertThat(updateResult).isEqualTo(true);
    }

    /**
     * Создается объект ticket и сохраняется в базе данных.
     * Выполняется изменение полей posRow и id с обновлением объекта ticket в
     * базе данных при помощи метода {@link JdbcTicketRepository#update(Ticket)}.
     * Результат обновления записывается в переменную updateResult.
     * По полю oldId объект ticket находится в базе данных, сохраняется в переменную ticketFromDB
     * при помощи метода {@link JdbcTicketRepository#findById(int)}, далее
     * проверяется отсутствие эквивалентности ticketFromDB объекту ticket
     * и переменной updateResult на эквивалентность false.
     */
    @Test
    public void whenUpdateTicketThenFalse() {
        int oldId = ticket.getId();
        ticket.setPosRow(ticket.getPosRow() + 1);
        ticket.setId(ticket.getId() + 1);
        boolean updateResult = ticketRepository.update(ticket);
        Ticket ticketFromDB = ticketRepository.findById(oldId).get();

        assertThat(ticket).isNotEqualTo(ticketFromDB);
        assertThat(updateResult).isEqualTo(false);
    }

    /**
     * Создаются объекты ticket, ticket2 и сохраняются в базе данных.
     * По полю id объект ticket находится в базе данных при помощи метода
     * {@link JdbcTicketRepository#findById(int)} и проверяется на эквивалентность
     * объекту ticket по полю name.
     */
    @Test
    public void whenFindTicketByIdThenGetTicketFromDatabase() {
        User user2 = User.builder()
                .username("username2")
                .email("email2")
                .phone("phone2")
                .password("pass2")
                .build();
        Show show2 = Show.builder()
                .name("name2")
                .description("description2")
                .posterName("poster2.jpg")
                .build();
        Ticket ticket2 = Ticket.builder()
                .show(show2)
                .posRow(2)
                .cell(2)
                .user(user2)
                .build();
        showRepository.save(show2);
        userRepository.save(user2);
        ticketRepository.save(ticket2);

        assertThat(ticketRepository.findById(ticket.getId()).get()).isNotNull()
                .satisfies(ticket -> {
                    assertThat(ticket.getShow().getId())
                            .isEqualTo(ticket.getShow().getId());
                    assertThat(ticket.getPosRow()).isEqualTo(ticket.getPosRow());
                    assertThat(ticket.getCell()).isEqualTo(ticket.getCell());
                });
    }

    /**
     * Создается объект ticket и сохраняется в базе данных.
     * По полю id объект ticket находится в базе данных при помощи
     * метода {@link JdbcTicketRepository#findById(int)} и
     * проверяется на эквивалентность Optional.empty().
     */
    @Test
    void whenFindTicketByIdThenDoNotGetTicketFromDatabase() {
        assertThat(ticketRepository.findById(ticket.getId() + 1)).isEqualTo(Optional.empty());
    }

    /**
     * Создается объект ticket и сохраняется в базе данных.
     * По полю id объект ticket удаляется из базы данных при помощи метода
     * {@link JdbcTicketRepository#deleteById(int)}
     * Метод {@link JdbcTicketRepository#deleteById(int)} при удалении
     * объекта возвращает true, вызов метода {@link JdbcTicketRepository#findById(int)}
     * проверяется на эквивалентность Optional.empty().
     */
    @Test
    public void whenDeleteTicketByIdThenDoNotGetTicketFromDatabase() {
        int id = ticket.getId();

        assertThat(ticketRepository.deleteById(id)).isEqualTo(true);
        assertThat(ticketRepository.findById(id)).isEqualTo(Optional.empty());
    }

    /**
     * Создаются объекты ticket, ticket1 и сохраняются в базе данных.
     * По полю show_id сеанса объекты ticket и ticket1 удаляются из базы данных при помощи метода
     * {@link JdbcTicketRepository#deleteTicketsByShowId(int)}
     * Метод {@link JdbcTicketRepository#deleteTicketsByShowId(int)} при удалении
     * объекта возвращает true, вызов метода {@link JdbcTicketRepository#findAll()}
     * должен вернуть пустую коллекцию.
     */
    @Test
    public void whenDeleteTicketByShowsIdThenDoNotGetTicketFromDatabase() {
        Ticket ticket2 = Ticket.builder()
                .show(show)
                .posRow(2)
                .cell(2)
                .user(user)
                .build();

        ticketRepository.save(ticket2);
        assertThat(ticketRepository.deleteTicketsByShowId(show.getId())).isEqualTo(true);
        assertThat(ticketRepository.findAll().size()).isEqualTo(0);
    }

    /**
     * Создаются объекты ticket, ticket2 и сохраняются в базе данных.
     * Через вызов метода {@link JdbcTicketRepository#findAll()}
     * получаем список объекты tickets, который сортируется по id.
     * Выполняем проверку размера списка и содержание элементов
     * на эквивалентность объектам ticket и ticket2 по полям name.
     */
    @Test
    public void whenFindAllTicketsThenGetListOfAllTickets() {
        User user2 = User.builder()
                .username("username2")
                .email("email2")
                .phone("phone2")
                .password("pass2")
                .build();
        Show show2 = Show.builder()
                .name("name2")
                .description("description2")
                .build();
        Ticket ticket2 = Ticket.builder()
                .show(show2)
                .posRow(2)
                .cell(2)
                .user(user2)
                .build();
        showRepository.save(show2);
        userRepository.save(user2);
        ticketRepository.save(ticket2);
        List<Ticket> tickets = ticketRepository.findAll();
        tickets.sort(Comparator.comparing(Ticket::getId));

        assertThat(tickets.size()).isEqualTo(2);
        assertThat(tickets.get(0)).isEqualTo(ticket);
        assertThat(tickets.get(1)).isEqualTo(ticket2);
    }
}