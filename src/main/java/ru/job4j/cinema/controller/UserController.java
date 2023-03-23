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
 * Контроллер пользователя
 *
 * @author Alexander Emelyanov
 * @version 1.0
 */
@Slf4j
@AllArgsConstructor
@Controller
public class UserController {

    /**
     * Объект для доступа к методам UserService
     */
    private final UserService userService;

    /**
     * Обрабатывает GET запрос, возвращает страницу редактирования пользователя.
     * В зависимости от параметров password и account на страницу будут выведены
     * сообщения для пользователя о необходимости исправить вводимые данные.
     *
     * @param password параметр GET запроса, true, если есть ошибка валидации пароля
     * @param phone    параметр GET запроса, true, если ошибка валидации номера телефона
     * @param model    модель
     * @param request  запрос пользователя
     * @return страница редактирования пользователя
     */
    @GetMapping("/userEdit")
    public String getUserEdit(@RequestParam(value = "password", required = false) String password,
                              @RequestParam(value = "phone", required = false) String phone,
                              Model model, HttpServletRequest request) {
        String errorMessage = null;
        if (password != null) {
            errorMessage = "Неверно введен старый пароль";
        }
        if (phone != null) {
            errorMessage = "Пользователь с таким номером телефона уже зарегистрирован";
        }
        model.addAttribute("user", UserUtil.getSessionUser(request));
        model.addAttribute("errorMessage", errorMessage);
        return "user/userEdit";
    }

    /**
     * Обрабатывает POST запрос, выполняется перенаправление на страницу списка сеансов.
     * При удачной валидации пользователя, пользователь обновляется в базе,
     * при неудачной валидации exceptionHandler контроллера выполняет переадресацию
     * на страницу регистрации с соответствующими параметрами.
     *
     * @param user        пользователь сформированный из данных формы редактирования
     * @param errors      список ошибок полученных при валидации модели пользователя
     * @param oldPassword старый пароль пользователя
     * @param request     запрос пользователя
     * @return перенаправление на страницу списка сеансов
     */
    @PostMapping("/userEdit")
    public String userEdit(@Valid @ModelAttribute User user, Errors errors,
                           @RequestParam(value = "oldPassword") String oldPassword,
                           HttpServletRequest request) {
        if (errors.hasErrors()) {
            return "user/userEdit";
        }
        User userFromDB = userService.findUserByEmail(user.getEmail());
        if (oldPassword == null || !oldPassword.equals(userFromDB.getPassword())) {
            return "redirect:/userEdit?password=true";
        }
        userService.update(user);
        request.getSession().setAttribute("user", user);
        return "redirect:/shows";
    }

    /**
     * Выполняет локальный (уровня контроллера) перехват исключений
     * IllegalStateException, в случае перехвата,
     * перенаправляет на страницу редактирования пользователя.
     *
     * @param e перехваченное исключение
     * @return перенаправление на страницу входа с параметром password=true
     */
    @ExceptionHandler(value = {IllegalStateException.class})
    public String illegalStateExceptionHandler(Exception e) {
        log.error(e.getLocalizedMessage());
        return "redirect:/userEdit?password=true";
    }

    /**
     * Выполняет локальный (уровня контроллера) перехват исключений
     * IllegalArgumentException, в случае перехвата,
     * перенаправляет на страницу редактирования пользователя.
     *
     * @param e перехваченное исключение
     * @return перенаправление на страницу входа с параметром phone=true
     */
    @ExceptionHandler(value = {IllegalArgumentException.class})
    public String illegalArgumentExceptionHandler(Exception e) {
        log.error(e.getLocalizedMessage());
        return "redirect:/userEdit?phone=true";
    }

}