package ru.job4j.cinema.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.job4j.cinema.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;

/**
 * Глобальный обработчик исключений
 * @see NoSuchElementException
 * @author Alexander Emelyanov
 * @version 1.0
 */
@Slf4j
@ControllerAdvice
public class NoSuchElementExceptionHandler {

    /**
     * Выполняет глобальный (уровня приложения) перехват исключений
     * NoSuchElementException, в случае перехвата, направляет информацию
     * об исключении на соответствующую веб страницу.
     *
     * @param e перехваченное исключение
     * @param request запрос пользователя
     * @return модель для передачи данных об исключении на веб страницу
     */
    @ExceptionHandler(value = {NoSuchElementException.class})
    public ModelAndView handleException(Exception e, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("error/404", "errorMessage", e.getMessage());
        User user = (User) request.getSession().getAttribute("user");
        modelAndView.addObject("user", user);
        log.error(e.getMessage());
        return modelAndView;
    }
}
