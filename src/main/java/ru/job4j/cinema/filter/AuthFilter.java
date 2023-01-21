package ru.job4j.cinema.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Сервлетный фильтр
 * @author Alexander Emelyanov
 * @version 1.0
 */
@Component
public class AuthFilter implements Filter {
    /**
     * Строковый список html ресурсов доступных без аутентификации
     */
    private final Set<String> mappings = new HashSet<>(
            Arrays.asList("login", "registration", "/"));

    /**
     * Выполняет проверку, находится ли запрашиваемый ресурс
     * в списке разрешенных без аутентификации. Если ресурс в разрешенном списке,
     * то передаем управление следующему в цепочке фильтру, иначе выполняется проверка
     * на наличие в сессии пользователя, если его нет, то выполняется перенаправление
     * на страницу с формами для входа пользователя. Если пользователь в сессии
     * присутствует, выполняется передача управления следующему в цепочке фильтру.
     *
     * @param request запрос пользователя
     * @param response ответ пользователю
     * @param chain цепочка фильтров
     * @throws IOException может выбрасывать исключение ввода вывода
     * @throws ServletException может выбрасывать исключение ввода вывода
     */
    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();
        if (isAllowed(uri)) {
            chain.doFilter(req, res);
            return;
        }
        if (req.getSession().getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        chain.doFilter(req, res);
    }

    /**
     * Вспомогательный метод проверяющий находится ли запрашиваемый ресурс
     * в списке ресурсов разрешенных без аутентификации.
     *
     * @param uri идентификатор пользователя
     * @return true при успешном нахождении, иначе false
     */
    private boolean isAllowed(String uri) {
        return mappings.stream().anyMatch(uri::endsWith);
    }
}
