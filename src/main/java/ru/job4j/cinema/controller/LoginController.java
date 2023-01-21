package ru.job4j.cinema.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;
import ru.job4j.cinema.util.UserUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.NoSuchElementException;

/**
 * Контроллер для страницы входа пользователя
 * @author Alexander Emelyanov
 * @version 1.0
 */
@Controller
@Slf4j
public class LoginController {

    /**
     * Объект для доступа к методам UserService
     */
    private final UserService userService;

    /**
     * Конструктор класса.
     *
     * @param userService объект для доступа к методам слоя UserService
     */
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Обрабатывает GET запрос, возвращает страницу входа пользователя.
     * В зависимости от параметров password и account на страницу будут выведены сообщения
     * для пользователя.
     *
     * @param error параметр GET запроса, true, если есть ошибка при заполнении формы
     * @param logout параметр GET запроса, true, если пользователь разлогинился
     * @param model модель
     * @param request запрос пользователя
     * @return страница входа пользователя
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model,
                            HttpServletRequest request) {
        String errorMessage = null;
        if (error != null) {
            errorMessage = "Имя аккаунта или пароль введены неправильно!";
        }
        if (logout != null) {
            errorMessage = "Вы вышли!";
        }
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("user", UserUtil.getSessionUser(request));
        return "user/login";
    }

    /**
     * Обрабатывает POST запрос, возвращает страницу со списком сеансов.
     * При удачной валидации пользователя, пользователь записывается
     * в аттрибуты сессии, при неудачной валидации exceptionHandler
     * контроллера выполняет переадресацию на страницу регистрации
     * с соответствующими параметрами.
     *
     * @param user параметр GET запроса, true, если есть ошибка при заполнении формы
     * @param request запрос пользователя
     * @return список киносеансов
     */
    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, HttpServletRequest request) {
        User userFromDB = userService.validateUserLogin(user);
        HttpSession session = request.getSession();
        session.setAttribute("user", userFromDB);
        return "redirect:/shows";
    }

    /**
     * Обрабатывает GET запрос, перенаправляет на страницу входа.
     * Выполняется выход пользователя из приложения и
     * очистка сессии.
     *
     * @param session сессия клиента
     * @return перенаправление на страницу входа с параметром logout=true
     */
    @GetMapping(value = "/logout")
    public String logoutPage(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }

    /**
     * Выполняет локальный (уровня контроллера) перехват исключений
     * IllegalArgumentException и NoSuchElementException, в случае перехвата,
     * перенаправляет на страницу входа.
     *
     * @param e перехваченное исключение
     * @return перенаправление на страницу входа с параметром error=true
     */
    @ExceptionHandler(value = {IllegalArgumentException.class, NoSuchElementException.class})
    public String exceptionHandler(Exception e) {
        log.error(e.getLocalizedMessage());
        return "redirect:/login?error=true";
    }
}
