package ru.job4j.cinema.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.job4j.cinema.model.User;

import javax.servlet.http.HttpServletRequest;

/**
 * Глобальный обработчик исключений
 *
 * @author Alexander Emelyanov
 * @version 1.0
 * @see IllegalArgumentException
 */
@Slf4j
@ControllerAdvice
public class IllegalArgumentExceptionHandler {

    /**
     * Выполняет глобальный (уровня приложения) перехват исключений
     * IllegalArgumentException, в случае перехвата, направляет информацию
     * об исключении на соответствующую веб страницу.
     *
     * @param e       перехваченное исключение
     * @param request запрос пользователя
     * @return модель для передачи данных об исключении на веб страницу
     */
    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ModelAndView handleException(Exception e, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("error/400", "errorMessage", e.getMessage());
        User user = (User) request.getSession().getAttribute("user");
        modelAndView.addObject("user", user);
        log.error(e.getMessage());
        return modelAndView;
    }
}
