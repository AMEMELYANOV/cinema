package ru.job4j.cinema.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import ru.job4j.cinema.model.Show;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.TicketService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

/**
 * Тест класс реализации контроллеров
 * @see ru.job4j.cinema.controller.TicketController
 * @author Alexander Emelyanov
 * @version 1.0
 */
class TicketControllerTest {

    /**
     * Объект для доступа к методам TicketController
     */
    private TicketController ticketController;

    /**
     * Объект для доступа к методам TicketService
     */
    private TicketService ticketService;

    /**
     * Пользователь
     */
    private User user;

    /**
     * Запрос
     */
    private HttpServletRequest request;

    /**
     * Сессия
     */
    private HttpSession session;

    /**
     * Модель
     */
    private Model model;

    /**
     * Сеанс
     */
    private Show show;

    /**
     * Билет
     */
    private Ticket ticket;

    /**
     * Создает необходимые для выполнения тестов общие объекты.
     * Создание выполняется перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        ticketService = mock(TicketService.class);
        ticketController = new TicketController(ticketService);
        model = mock(Model.class);
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        user = User.builder()
                .id(1)
                .username("username")
                .email("email")
                .password("password")
                .phone("111111111")
                .build();
        show = Show.builder()
                .id(1)
                .name("name")
                .build();
        ticket = Ticket.builder()
                .id(1)
                .show(show)
                .posRow(1)
                .cell(1)
                .build();
    }

    /**
     * Выполняется проверка возвращения страницы покупки билета,
     * при выборе места в ряду.
     */
    @Test
    void whenBookTicketSuccess() {
        int cell = ticket.getCell();
        int posRow = ticket.getPosRow();
        doReturn(session).when(request).getSession();
        doReturn(show).when(session).getAttribute("show");
        doReturn(posRow).when(session).getAttribute("posRow");
        doReturn(cell).when(session).getAttribute("cell");
        doReturn(user).when(session).getAttribute("user");

        String result = ticketController.bookTicket(cell, model, request);

        verify(session).setAttribute("cell", cell);
        verify(model).addAttribute("show", show);
        verify(model).addAttribute("posRow", posRow);
        verify(model).addAttribute("cell", cell);
        verify(model).addAttribute("user", user);
        Assertions.assertThat(result).isEqualTo("ticket/buyTicket");
    }

    /**
     * Выполняется проверка возврата на страницу списка сеансов,
     * при отказе от покупки билета.
     */
    @Test
    void whenCancelBuyTicketSuccessThenRedirectShows() {
        String result = ticketController.cancelBuyTicket();

        Assertions.assertThat(result).isEqualTo("redirect:/shows");
    }

    /**
     * Выполняется проверка возвращения страницы выполненной покупки билета.
     */
    @Test
    void whenConfirmBuyTicketSuccess() {
        int cell = ticket.getCell();
        int posRow = ticket.getPosRow();
        doReturn(session).when(request).getSession();
        doReturn(show).when(session).getAttribute("show");
        doReturn(posRow).when(session).getAttribute("posRow");
        doReturn(cell).when(session).getAttribute("cell");
        doReturn(user).when(session).getAttribute("user");
        doReturn(ticket).when(ticketService).save(new Ticket());

        String result = ticketController.confirmBuyTicket(model, request);

        verify(model).addAttribute("ticket", ticket);
        verify(model).addAttribute("user", user);
        Assertions.assertThat(result).isEqualTo("ticket/successful");
    }


    /**
     * Выполняется проверка возвращения страницы списка сеансов для администратора,
     * после очистки списка проданных билетов.
     */
    @Test
    void whenClearTicketsSuccessThenRedirectToAdminShows() {
        int showId = 1;
        doReturn(session).when(request).getSession();
        doReturn(user).when(session).getAttribute("user");

        String result = ticketController.clearTickets(showId, model, request);

        verify(model).addAttribute("user", user);
        Assertions.assertThat(result).isEqualTo("redirect:/adminShows");
    }
}