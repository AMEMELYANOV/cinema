package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.job4j.cinema.model.Show;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.JdbcShowRepository;
import ru.job4j.cinema.repository.TicketRepository;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doReturn;

/**
 * Тест класс реализации сервисного слоя билетов
 *
 * @author Alexander Emelyanov
 * @version 1.0
 * @see JdbcShowRepository
 */
class ImplTicketServiceTest {

    /**
     * Объект для доступа к методам TicketRepository
     */
    private TicketRepository ticketRepository;

    /**
     * Объект для доступа к методам ImplTicketService
     */
    private ImplTicketService ticketService;

    /**
     * Сеанс
     */
    private Show show;

    /**
     * Пользователь
     */
    private User user;

    /**
     * Билет
     */
    private Ticket ticket;

    /**
     * Создает необходимые для выполнения тестов общие объекты.
     * Создание выполняется перед каждым тестом.
     */
    @BeforeEach
    public void setup() {
        ticketRepository = Mockito.mock(TicketRepository.class);
        ticketService = new ImplTicketService(ticketRepository);
        show = Show.builder()
                .id(1)
                .name("Show")
                .description("Description")
                .posterName("Poster")
                .build();
        user = User.builder()
                .id(1)
                .username("user")
                .email("user@mail.ru")
                .password("123")
                .phone("+79000000000")
                .build();
        ticket = Ticket.builder()
                .id(1)
                .show(show)
                .user(user)
                .posRow(1)
                .cell(1)
                .build();

    }

    /**
     * Выполняется проверка возвращения списка билетов
     * от ticketRepository, если в списке есть элементы.
     */
    @Test
    void whenFindAllThenReturnList() {
        show = Show.builder()
                .id(1)
                .name("Show")
                .description("Description")
                .posterName("Poster")
                .build();
        user = User.builder()
                .id(1)
                .username("user")
                .email("user@mail.ru")
                .password("123")
                .phone("+79000000000")
                .build();
        Ticket ticket1 = Ticket.builder()
                .id(1)
                .show(show)
                .user(user)
                .posRow(1)
                .cell(1)
                .build();

        List<Ticket> tickets = List.of(ticket, ticket1);
        doReturn(tickets).when(ticketRepository).findAll();
        List<Ticket> ticketList = ticketService.findAll();

        assertThat(ticketList).isNotNull();
        assertThat(ticketList.size()).isEqualTo(2);
    }

    /**
     * Выполняется проверка возвращения списка билетов
     * от ticketRepository, если список пустой.
     */
    @Test
    void whenFindAllThenReturnEmptyList() {
        doReturn(Collections.emptyList()).when(ticketRepository).findAll();
        List<Ticket> ticketList = ticketService.findAll();

        assertThat(ticketList).isEmpty();
        assertThat(ticketList.size()).isEqualTo(0);
    }

    /**
     * Выполняется проверка возвращения билета, при возврате
     * от ticketRepository Optional.of(ticket), т.е. если билет найден по идентификатору.
     */
    @Test
    void whenFindByIdThenReturnTicket() {
        doReturn(Optional.of(ticket)).when(ticketRepository).findById(1);
        Ticket ticketFromDB = ticketService.findById(1);

        assertThat(ticketFromDB).isEqualTo(ticket);
        assertThat(ticketFromDB).isNotNull();
    }

    /**
     * Выполняется проверка выброса исключения, при возврате от
     * ticketRepository Optional.empty(), если билет не найден по идентификатору.
     */
    @Test
    void whenFindByIdThenThrowsException() {
        doReturn(Optional.empty()).when(ticketRepository).findById(anyInt());

        assertThrows(NoSuchElementException.class, () -> ticketService.findById(anyInt()));
    }

    /**
     * Выполняется проверка возврата билета, при возврате от
     * ticketRepository Optional.of(ticket), т.е. если билет был сохранен.
     */
    @Test
    void whenSaveThenReturnticket() {
        doReturn(Optional.of(ticket)).when(ticketRepository).save(ticket);
        Ticket ticketFromDB = ticketService.save(ticket);

        assertThat(ticketFromDB).isEqualTo(ticket);
        assertThat(ticketFromDB).isNotNull();
    }

    /**
     * Выполняется проверка выброса исключения, при возврате от
     * ticketRepository Optional.empty(), если билет не был сохранен.
     */
    @Test
    void whenSaveThenThrowsException() {
        doReturn(Optional.empty()).when(ticketRepository).save(ticket);

        assertThrows(IllegalArgumentException.class, () -> ticketService.save(ticket));
    }

    /**
     * Выполняется проверка обновление билета, при возврате от
     * ticketRepository true, т.е. если билет был сохранен.
     */
    @Test
    void whenUpdateThenReturnTrue() {
        doReturn(true).when(ticketRepository).update(ticket);
        boolean result = ticketService.update(ticket);

        assertThat(result).isEqualTo(true);
    }

    /**
     * Выполняется проверка выброса исключения, при возврате от
     * ticketRepository false, т.е. если билет не был обновлен.
     */
    @Test
    void whenUpdateThenThrowsException() {
        doReturn(false).when(ticketRepository).update(ticket);

        assertThrows(NoSuchElementException.class, () -> ticketService.update(ticket));
    }

    /**
     * Выполняется проверка удаления билета, при возврате
     * от ticketRepository true, т.е. если билет удален по идентификатору.
     */
    @Test
    void whenDeleteByIdThenReturnTrue() {
        doReturn(true).when(ticketRepository).deleteById(anyInt());
        boolean result = ticketService.deleteById(anyInt());

        assertThat(result).isEqualTo(true);
    }

    /**
     * Выполняется проверка выброса исключения, при возврате
     * от ticketRepository false, т.е. если билет не удален или
     * не найден по идентификатору.
     */
    @Test
    void whenDeleteByIdThenThrowsException() {
        doReturn(false).when(ticketRepository).deleteById(anyInt());

        assertThrows(NoSuchElementException.class, () -> ticketService.deleteById(anyInt()));
    }

    /**
     * Выполняется проверка удаления билетов по идентификатору сеанса, при возврате
     * от ticketRepository true, т.е. если билет удален по идентификатору.
     */
    @Test
    void whenDeleteTicketsByShowIdThenReturnTrue() {
        doReturn(true).when(ticketRepository).deleteTicketsByShowId(anyInt());
        boolean result = ticketService.deleteTicketsByShowId(anyInt());

        assertThat(result).isEqualTo(true);
    }

    /**
     * Выполняется проверка выброса исключения, при возврате от ticketRepository
     * false, т.е. если билеты по идентификатору сеанса не удалены или
     * не найдены.
     */
    @Test
    void whenDeleteTicketsByShowIdThrowsException() {
        doReturn(false).when(ticketRepository).deleteById(anyInt());

        assertThrows(NoSuchElementException.class, () -> ticketService.deleteById(anyInt()));
    }
}