package ru.job4j.cinema.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер для стартовой страницы приложения
 *
 * @author Alexander Emelyanov
 * @version 1.0
 */
@AllArgsConstructor
@Controller
public class IndexController {

    /**
     * Возвращает стартовую страницу приложения.
     *
     * @return стартовая страница приложения
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }
}