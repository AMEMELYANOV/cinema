package ru.job4j.cinema.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.job4j.cinema.model.Show;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.TicketService;
import ru.job4j.cinema.util.UserUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Контроллер билетов
 *
 * @author Alexander Emelyanov
 * @version 1.0
 */
@AllArgsConstructor
@Controller
public class TicketController {

    /**
     * Объект для доступа к методам TicketService
     */
    private final TicketService ticketService;

    /**
     * Обрабатывает POST запрос, возвращает страницу покупки билета.
     *
     * @param cell    пользователь сформированный из данных формы редактирования
     * @param model   список ошибок полученных при валидации модели пользователя
     * @param request запрос пользователя
     * @return страница покупки билета
     */
    @PostMapping("/buyTicket")
    public String bookTicket(@RequestParam(value = "cell") int cell,
                             Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("cell", cell);
        model.addAttribute("show", session.getAttribute("show"));
        model.addAttribute("posRow", session.getAttribute("posRow"));
        model.addAttribute("cell", session.getAttribute("cell"));
        model.addAttribute("user", UserUtil.getSessionUser(request));
        return "ticket/buyTicket";
    }

    /**
     * Обрабатывает POST запрос, отменяет покупку билета и выполняет перенаправление
     * на страницу списка сеансов.
     *
     * @return перенаправление на страницу списка сеансов
     */
    @PostMapping("/cancelBuyTicket")
    public String cancelBuyTicket() {
        return "redirect:/shows";
    }

    /**
     * Обрабатывает POST запрос, выполняет покупку билета и возвращает
     * страницу информации о выполненной покупке, если билет куплен другим
     * пользователем произойдет перенаправление на соответствующую веб
     * страницу с описанием ошибки.
     *
     * @param model   модель
     * @param request запрос пользователя
     * @return возвращает страницу информации о выполненной покупке
     */
    @PostMapping("/confirmBuyTicket")
    public String confirmBuyTicket(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Ticket ticket = ticketService.save(Ticket
                .builder()
                .user((User) session.getAttribute("user"))
                .show((Show) session.getAttribute("show"))
                .posRow((int) session.getAttribute("posRow"))
                .cell((int) session.getAttribute("cell"))
                .build());
        model.addAttribute("ticket", ticket);
        model.addAttribute("user", UserUtil.getSessionUser(request));
        return "ticket/successful";
    }

    /**
     * Обрабатывает POST запрос, удаляет проданные билеты и перенаправляет на страницу
     * списка сеансов для администратора.
     *
     * @param showId  модель
     * @param model   модель
     * @param request запрос пользователя
     * @return возвращает страницу информации о выполненной покупке
     */
    @PostMapping("/clearTickets")
    public String clearTickets(@RequestParam(value = "showId") int showId,
                               Model model, HttpServletRequest request) {
        ticketService.deleteTicketsByShowId(showId);
        model.addAttribute("user", UserUtil.getSessionUser(request));
        return "redirect:/adminShows";
    }
}