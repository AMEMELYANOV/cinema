package ru.job4j.cinema.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

/**
 * Тест класс реализации контроллеров
 *
 * @author Alexander Emelyanov
 * @version 1.0
 * @see ru.job4j.cinema.controller.UserController
 */
class UserControllerTest {

    /**
     * Объект для доступа к методам UserController
     */
    private UserController userController;

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
     * Ошибки валидации
     */
    private Errors errors;

    /**
     * Создает необходимые для выполнения тестов общие объекты.
     * Создание выполняется перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        model = mock(Model.class);
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        errors = mock(Errors.class);
        user = User.builder()
                .id(1)
                .username("username")
                .email("email")
                .password("password")
                .phone("111111111")
                .build();
    }

    /**
     * Выполняется проверка возвращения страницы редактирования пользователя.
     */
    @Test
    void whenGetUserEditPage() {
        String password = null;
        String phone = null;
        String errorMessage = null;
        doReturn(session).when(request).getSession();
        doReturn(user).when(session).getAttribute("user");

        String result = userController.getUserEdit(password, phone, model, request);

        verify(model).addAttribute("errorMessage", errorMessage);
        verify(model).addAttribute("user", user);
        Assertions.assertThat(result).isEqualTo("user/userEdit");
    }

    /**
     * Выполняется проверка возвращения страницы редактирования пользователя,
     * при неверно введенном старом пароле.
     */
    @Test
    void whenGetUserEditPageIfPasswordParameterNotNullThenError() {
        String password = "true";
        String phone = null;
        String errorMessage = "Неверно введен старый пароль";
        doReturn(session).when(request).getSession();
        doReturn(user).when(session).getAttribute("user");

        String result = userController.getUserEdit(password, phone, model, request);

        verify(model).addAttribute("errorMessage", errorMessage);
        verify(model).addAttribute("user", user);
        Assertions.assertThat(result).isEqualTo("user/userEdit");
    }

    /**
     * Выполняется проверка возвращения страницы редактирования пользователя,
     * при ранее зарегистрированном телефоне пользователя.
     */
    @Test
    void whenGetUserEditPageIfPhoneParameterNotNullThenError() {
        String password = null;
        String phone = "true";
        String errorMessage = "Пользователь с таким номером телефона уже зарегистрирован";
        doReturn(session).when(request).getSession();
        doReturn(user).when(session).getAttribute("user");

        String result = userController.getUserEdit(password, phone, model, request);

        verify(model).addAttribute("errorMessage", errorMessage);
        verify(model).addAttribute("user", user);
        Assertions.assertThat(result).isEqualTo("user/userEdit");
    }

    /**
     * Выполняется проверка возвращения страницы редактирования пользователя,
     * при ошибках ввода пароля в форму редактирования пользователя.
     */
    @Test
    void whenUserEditSuccessThenRedirectToShows() {
        String oldPassword = user.getPassword();
        doReturn(session).when(request).getSession();
        doReturn(user).when(userService).findUserByEmail(anyString());

        String result = userController.userEdit(user, errors, oldPassword, request);

        verify(session).setAttribute("user", user);
        Assertions.assertThat(result).isEqualTo("redirect:/shows");
    }

    /**
     * Выполняется проверка возвращения страницы редактирования пользователя,
     * при ошибках ввода данных в форму редактирования пользователя.
     */
    @Test
    void whenUserEditIfErrorsParameterNotNullThenError() {
        String oldPassword = user.getPassword();
        doReturn(session).when(request).getSession();
        doReturn(user).when(userService).findUserByEmail(anyString());
        doReturn(true).when(errors).hasErrors();

        String result = userController.userEdit(user, errors, oldPassword, request);

        Assertions.assertThat(result).isEqualTo("user/userEdit");
    }

    /**
     * Выполняется проверка возвращения страницы списка сеансов,
     * при ошибке ввода пароля в форму редактирования пользователя.
     */
    @Test
    void whenUserEditIfOldPasswordFailThenError() {
        String oldPassword = "pass";
        doReturn(session).when(request).getSession();
        doReturn(user).when(userService).findUserByEmail(anyString());

        String result = userController.userEdit(user, errors, oldPassword, request);

        Assertions.assertThat(result).isEqualTo("redirect:/userEdit?password=true");
    }
}