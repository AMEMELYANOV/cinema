package ru.job4j.cinema.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import ru.job4j.cinema.model.Show;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.ShowService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Тест класс реализации контроллеров
 * @see ru.job4j.cinema.controller.ShowController
 * @author Alexander Emelyanov
 * @version 1.0
 */
class ShowControllerTest {

    /**
     * Объект для доступа к методам ShowController
     */
    private ShowController showController;

    /**
     * Объект для доступа к методам ShowService
     */
    private ShowService showService;

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
     * Создает необходимые для выполнения тестов общие объекты.
     * Создание выполняется перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        showService = mock(ShowService.class);
        showController = new ShowController(showService);
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
    }

    /**
     * Выполняется проверка возвращения страницы списка сеансов для
     * покупки билетов
     */
    @Test
    void whenGetShowRowSuccess() {
        List<Show> shows = new ArrayList<>();
        doReturn(session).when(request).getSession();
        doReturn(user).when(session).getAttribute("user");
        doReturn(shows).when(showService).findAll();

        String result = showController.getShowRow(model, request);

        verify(model).addAttribute("shows", shows);
        verify(model).addAttribute("user", user);
        Assertions.assertThat(result).isEqualTo("show/shows");
    }


    /**
     * Выполняется проверка возвращения страницы списка сеансов для
     * администратора.
     */
    @Test
    void whenGetShowAdminSuccess() {
        List<Show> shows = new ArrayList<>();
        doReturn(session).when(request).getSession();
        doReturn(user).when(session).getAttribute("user");
        doReturn(shows).when(showService).findAll();

        String result = showController.getShowsForEdit(model, request);

        verify(model).addAttribute("shows", shows);
        verify(model).addAttribute("user", user);
        Assertions.assertThat(result).isEqualTo("admin/adminShows");
    }

    /**
     * Выполняется проверка возвращения страницы выбора
     * номера ряда на сеансе для покупки билетов.
     */
    @Test
    void whenPostShowRowSuccess() {
        int showId = 1;
        List<Integer> rows = new ArrayList<>();
        doReturn(show).when(showService).findById(showId);
        doReturn(session).when(request).getSession();
        doReturn(user).when(session).getAttribute("user");
        doReturn(rows).when(showService).findAll();

        String result = showController.getPostShowRow(showId, model, request);

        verify(model).addAttribute("show", show);
        verify(model).addAttribute("rows", rows);
        verify(model).addAttribute("user", user);
        verify(session).setAttribute("show", show);
        Assertions.assertThat(result).isEqualTo("show/showRow");
    }

    /**
     * Выполняется проверка возвращения страницы выбора
     * номера места в ряду на сеансе для покупки билетов.
     */
    @Test
    void whenSetShowRowSuccess() {
        int posRow = 1;
        List<Integer> cells = new ArrayList<>();
        doReturn(session).when(request).getSession();
        doReturn(user).when(session).getAttribute("user");
        doReturn(cells).when(showService).getCells(show.getId(), posRow);
        doReturn(show).when(session).getAttribute("show");

        String result = showController.setShowRow(posRow, model, request);

        verify(session).setAttribute("posRow", posRow);
        verify(model).addAttribute("show", show);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("cells", cells);
        Assertions.assertThat(result).isEqualTo("show/showCell");
    }

    /**
     * Выполняется проверка возвращения страницы выбора
     * редактирования данных сеанса.
     */
    @Test
    void whenEditShowSuccess() {
        int showId = 1;
        doReturn(session).when(request).getSession();
        doReturn(user).when(session).getAttribute("user");
        doReturn(show).when(showService).findById(showId);

        String result = showController.editShow(showId, model, request);

        verify(model).addAttribute("show", show);
        verify(model).addAttribute("user", user);
        Assertions.assertThat(result).isEqualTo("admin/editShow");
    }

    /**
     * Выполняется проверка возвращения страницы выбора
     * добавления нового сеанса.
     */
    @Test
    void whenAddShowSuccess() {
        doReturn(session).when(request).getSession();
        doReturn(user).when(session).getAttribute("user");

        String result = showController.addShow(model, request);

        verify(model).addAttribute("show", new Show());
        verify(model).addAttribute("user", user);
        Assertions.assertThat(result).isEqualTo("admin/addShow");
    }
}