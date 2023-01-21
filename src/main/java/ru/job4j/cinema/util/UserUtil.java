package ru.job4j.cinema.util;

import ru.job4j.cinema.model.User;

import javax.servlet.http.HttpServletRequest;

/**
 * Утилитный класс для извлечения пользователя и сессии
 * @author Alexander Emelyanov
 * @version 1.0
 */
public final class UserUtil {

    /**
     * Приватный конструктор класса для исключения наследования.
     *
     *@exception AssertionError выбрасывается при попытке вызвать конструктор
     */
    private UserUtil() {
        throw new AssertionError();
    }

    /**
     * Выполняет извлечение пользователя из сессии, если пользователь в
     * сессии отсутствует, создается и возвращается новый пустой пользователь.
     *
     * @param req запрос пользователя
     * @return пользователя
     */
    public static User getSessionUser(HttpServletRequest req) {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            user = new User();
        }
        return user;
    }
}
