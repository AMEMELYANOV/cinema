package ru.job4j.cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основной класс для запуска приложения
 * @author Alexander Emelyanov
 * @version 1.0
 */
@SpringBootApplication
public class CinemaApplication {

    /**
     * Выполняет запуск приложения
     * @param args аргументы командной строки
     */
     public static void main(String[] args) {
        SpringApplication.run(CinemaApplication.class, args);
        System.out.println("Go to http://localhost:8080");
    }
}
