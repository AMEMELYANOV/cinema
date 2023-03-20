package ru.job4j.cinema.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;
import ru.job4j.cinema.util.UserUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Контроллер страницы регистрации пользователя
 * @author Alexander Emelyanov
 * @version 1.0
 */
@Slf4j
@AllArgsConstructor
@RequestMapping("/registration")
@Controller
public class RegController {

    /**
     * Объект для доступа к методам UserService
     */
    private final UserService userService;

    /**
     * Обрабатывает GET запрос, возвращает страницу регистрации пользователя.
     * В зависимости от параметров password и account на страницу будут выведены
     * сообщения для пользователя о необходимости исправить вводимые данные.
     *
     * @param password параметр GET запроса, true, если есть ошибка валидации пароля
     * @param account параметр GET запроса, true, если ошибка валидации
     * @param model модель
     * @param request запрос пользователя
     * @return страница регистрации пользователя
     */
    @GetMapping
    public String regPage(@RequestParam(value = "password", required = false) String password,
                          @RequestParam(value = "account", required = false) String account,
                          Model model,
                          HttpServletRequest request) {
        String errorMessage = null;
        if (password != null) {
            errorMessage = "Пароли должны совпадать!";
        }
        if (account != null) {
            errorMessage = "Пользователь с таким email или номером телефона уже зарегистрирован!";
        }
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("user", UserUtil.getSessionUser(request));
        return "user/registration";
    }

    /**
     * Обрабатывает POST запрос, выполняется перенаправление на страницу входа.
     * При удачной валидации пользователя, пользователь сохраняется в базе,
     * при неудачной валидации exceptionHandler контроллера выполняет переадресацию
     * на страницу регистрации с соответствующими параметрами.
     *
     * @param user пользователь сформированный из данных формы регистрации
     * @param errors список ошибок полученных при валидации модели пользователя
     * @param repassword повторно набранный пароль
     * @return страница входа пользователя
     */
    @PostMapping
    public String regSave(@Valid @ModelAttribute User user, Errors errors,
                          @RequestParam String repassword) {
        if (errors.hasErrors()) {
            return "user/registration";
        }
        if (!user.getPassword().equals(repassword)) {
            return "redirect:/registration?password=true";
        }
        userService.save(user);
        return "redirect:/login";
    }

    /**
     * Выполняет локальный (уровня контроллера) перехват исключений
     * IllegalStateException, в случае перехвата,
     * перенаправляет на страницу регистрации пользователя.
     *
     * @param e перехваченное исключение
     * @return перенаправление на страницу входа с параметром password=true
     */
    @ExceptionHandler(value = {IllegalStateException.class})
    public String illegalStateExceptionHandler(Exception e) {
        log.error(e.getLocalizedMessage());
        return "redirect:/registration?password=true";
    }

    /**
     * Выполняет локальный (уровня контроллера) перехват исключений
     * IllegalArgumentException, в случае перехвата,
     * перенаправляет на страницу регистрации пользователя.
     *
     * @param e перехваченное исключение
     * @return перенаправление на страницу входа с параметром account=true
     */
    @ExceptionHandler(value = {IllegalArgumentException.class})
    public String illegalArgumentExceptionHandler(Exception e) {
        log.error(e.getLocalizedMessage());
        return "redirect:/registration?account=true";
    }
}