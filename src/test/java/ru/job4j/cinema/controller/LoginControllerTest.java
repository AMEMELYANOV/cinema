package ru.job4j.cinema.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

/**
 * Тест класс реализации контроллеров
 * @see ru.job4j.cinema.controller.LoginController
 * @author Alexander Emelyanov
 * @version 1.0
 */
class LoginControllerTest {

    /**
     * Объект для доступа к методам IndexController
     */
    private LoginController loginController;

    /**
     * Объект для доступа к методам UserService
     */
    private UserService userService;

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
     * Создает необходимые для выполнения тестов общие объекты.
     * Создание выполняется перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        loginController = new LoginController(userService);
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
    }

    /**
     * Выполняется проверка возвращения страницы входа.
     */
    @Test
    void whenGetLoginPage() {
        String error = null;
        String logout = null;
        String errorMessage = null;
        doReturn(session).when(request).getSession();
        doReturn(user).when(session).getAttribute("user");

        String result = loginController.loginPage(error, logout, model, request);

        verify(model).addAttribute("errorMessage", errorMessage);
        verify(model).addAttribute("user", user);
        Assertions.assertThat(result).isEqualTo("user/login");
    }

    /**
     * Выполняется проверка возвращения страницы входа,
     * при наличии пользовательских ошибок входа.
     */
    @Test
    void whenGetLoginPageIfErrorParameterNotNullThenError() {
        String error = "true";
        String logout = null;
        String errorMessage = "Имя аккаунта или пароль введены неправильно!";
        doReturn(session).when(request).getSession();
        doReturn(user).when(session).getAttribute("user");

        String result = loginController.loginPage(error, logout, model, request);

        verify(model).addAttribute("errorMessage", errorMessage);
        verify(model).addAttribute("user", user);
        Assertions.assertThat(result).isEqualTo("user/login");
    }

    /**
     * Выполняется проверка возвращения страницы входа,
     * после выхода пользователя.
     */
    @Test
    void whenGetLoginPageIfLogoutParameterNotNullThenError() {
        String error = null;
        String logout = "true";
        String errorMessage = "Вы вышли!";
        doReturn(session).when(request).getSession();
        doReturn(user).when(session).getAttribute("user");

        String result = loginController.loginPage(error, logout, model, request);

        verify(model).addAttribute("errorMessage", errorMessage);
        verify(model).addAttribute("user", user);
        Assertions.assertThat(result).isEqualTo("user/login");
    }

    /**
     * Выполняется проверка возвращения страницы со списком сеансов,
     * после входа пользователя.
     */
    @Test
    void whenLoginUserSuccessThenShows() {
        doReturn(session).when(request).getSession();
        doReturn(user).when(userService).validateUserLogin(user);

        String result = loginController.loginUser(user, request);

        verify(session).setAttribute("user", user);
        Assertions.assertThat(result).isEqualTo("redirect:/shows");
    }

    /**
     * Выполняется проверка возвращения страницы входа,
     * после выхода пользователя.
     */
    @Test
    void whenUserLogoutThenRedirectToLogin() {
        String result = loginController.logoutPage(session);

        verify(session).invalidate();
        Assertions.assertThat(result).isEqualTo("redirect:/login?logout=true");
    }
}