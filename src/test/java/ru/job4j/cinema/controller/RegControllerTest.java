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
 * @see ru.job4j.cinema.controller.RegController
 */
class RegControllerTest {

    /**
     * Объект для доступа к методам RegController
     */
    private RegController regController;

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
        regController = new RegController(userService);
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
     * Выполняется проверка возвращения страницы регистрации.
     */
    @Test
    void whenGetRegPage() {
        String password = null;
        String account = null;
        String errorMessage = null;
        doReturn(session).when(request).getSession();
        doReturn(user).when(session).getAttribute("user");

        String result = regController.regPage(password, account, model, request);

        verify(model).addAttribute("errorMessage", errorMessage);
        verify(model).addAttribute("user", user);
        Assertions.assertThat(result).isEqualTo("user/registration");
    }

    /**
     * Выполняется проверка возвращения страницы регистрации,
     * при наличии пользовательских ошибок ввода пароля.
     */
    @Test
    void whenGetRegPageIfPasswordParameterNotNullThenError() {
        String password = "true";
        String account = null;
        String errorMessage = "Пароли должны совпадать!";
        doReturn(session).when(request).getSession();
        doReturn(user).when(session).getAttribute("user");

        String result = regController.regPage(password, account, model, request);

        verify(model).addAttribute("errorMessage", errorMessage);
        verify(model).addAttribute("user", user);
        Assertions.assertThat(result).isEqualTo("user/registration");
    }

    /**
     * Выполняется проверка возвращения страницы регистрации,
     * при наличии в системе пользователя с такими же учетными данными.
     */
    @Test
    void whenGetRegPageIfAccountParameterNotNullThenError() {
        String password = null;
        String account = "true";
        String errorMessage = "Пользователь с таким email или номером "
                + "телефона уже зарегистрирован!";
        doReturn(session).when(request).getSession();
        doReturn(user).when(session).getAttribute("user");

        String result = regController.regPage(password, account, model, request);

        verify(model).addAttribute("errorMessage", errorMessage);
        verify(model).addAttribute("user", user);
        Assertions.assertThat(result).isEqualTo("user/registration");
    }

    /**
     * Выполняется проверка возвращения страницы входа,
     * при удачной регистрации пользователя.
     */
    @Test
    void whenRegSaveSuccessThenRedirectToLogin() {
        String repassword = user.getPassword();

        String result = regController.regSave(user, errors, repassword);

        Assertions.assertThat(result).isEqualTo("redirect:/login");
    }

    /**
     * Выполняется проверка возвращения страницы входа,
     * при наличии ошибок заполнения формы регистрации пользователем.
     */
    @Test
    void whenRegSaveIfErrorsParameterNotNullThenRedirectToRegistration() {
        String repassword = user.getPassword();
        doReturn(true).when(errors).hasErrors();

        String result = regController.regSave(user, errors, repassword);

        Assertions.assertThat(result).isEqualTo("user/registration");
    }

    /**
     * Выполняется проверка возвращения страницы входа,
     * при не совпадении паролей формы регистрации пользователя.
     */
    @Test
    void whenRegSaveIfRepasswordFailThenRedirectToRegistration() {
        String repassword = "pass";

        String result = regController.regSave(user, errors, repassword);

        Assertions.assertThat(result).isEqualTo("redirect:/registration?password=true");
    }
}