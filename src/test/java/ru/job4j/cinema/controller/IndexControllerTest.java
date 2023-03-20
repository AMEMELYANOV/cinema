package ru.job4j.cinema.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Тест класс реализации контроллеров
 * @see ru.job4j.cinema.controller.IndexController
 * @author Alexander Emelyanov
 * @version 1.0
 */
class IndexControllerTest {

    /**
     * Объект для доступа к методам IndexController
     */
    private IndexController indexController;

    /**
     * Создает необходимые для выполнения тестов общие объекты.
     * Создание выполняется перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        indexController = new IndexController();
    }

    /**
     * Выполняется проверка возвращения индексной страницы.
     */
    @Test
    void getIndex() {
        String result  = indexController.index();

        Assertions.assertThat(result).isEqualTo("index");
    }
}