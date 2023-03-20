package ru.job4j.cinema.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import ru.job4j.cinema.model.Show;
import ru.job4j.cinema.service.ShowService;
import ru.job4j.cinema.util.UserUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Контроллер сеансов
 * @author Alexander Emelyanov
 * @version 1.0
 */
@Slf4j
@AllArgsConstructor
@Controller
public class ShowController {

    /**
     * Объект для доступа к методам UserService
     */
    private final ShowService showService;

    /**
     * Обрабатывает GET запрос, возвращает страницу списка сеансов для
     * покупки билетов.
     *
     * @param model модель
     * @param request запрос пользователя
     * @return страница списка сеансов
     */
    @GetMapping("/shows")
    public String getShowRow(Model model, HttpServletRequest request) {
        model.addAttribute("user", UserUtil.getSessionUser(request));
        model.addAttribute("shows", showService.findAll());
        return "show/shows";
    }

    /**
     * Обрабатывает GET запрос, возвращает страницу списка сеансов для
     * администратора.
     *
     * @param model модель
     * @param request запрос пользователя
     * @return страница списка сеансов для редактирования
     */
    @GetMapping("adminShows")
    public String getShowsForEdit(Model model, HttpServletRequest request) {
        model.addAttribute("user", UserUtil.getSessionUser(request));
        model.addAttribute("shows", showService.findAll());
        return "admin/adminShows";
    }

    /**
     * Обрабатывает POST запрос, возвращает страницу выбора
     * номера ряда на сеансе для покупки билетов.
     *
     * @param showId идентификатор сеанса
     * @param model модель
     * @param request запрос пользователя
     * @return страница выбора ряда
     */
    @PostMapping("/showRow")
    public String getPostShowRow(@RequestParam(value = "showId") int showId,
                        Model model, HttpServletRequest request) {
        Show showFromDB = showService.findById(showId);
        model.addAttribute("show", showFromDB);
        List<Integer> rows = showService.getRows(showId);
        model.addAttribute("rows", rows);
        HttpSession session = request.getSession();
        session.setAttribute("show", showFromDB);
        model.addAttribute("user", UserUtil.getSessionUser(request));

        return "show/showRow";
    }

    /**
     * Обрабатывает POST запрос, возвращает страницу выбора
     * номера места в ряду на сеансе для покупки билетов.
     *
     * @param posRow идентификатор ряда
     * @param model модель
     * @param request запрос пользователя
     * @return страница выбора места
     */
    @PostMapping("/showCell")
    public String setShowRow(@RequestParam(value = "posRow") int posRow,
                        Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("posRow", posRow);
        Show show = (Show) session.getAttribute("show");
        model.addAttribute("show", show);
        model.addAttribute("cells", showService.getCells(show.getId(), posRow));
        model.addAttribute("user", UserUtil.getSessionUser(request));
        return "show/showCell";
    }

    /**
     * Обрабатывает POST запрос, возвращает страницу
     * редактирования данных сеанса.
     *
     * @param showId идентификатор сеанса
     * @param model модель
     * @param request запрос пользователя
     * @return страница редактирования сеанса
     */
    @PostMapping("/editShow")
    public String editShow(@RequestParam(value = "showId") int showId,
                        Model model, HttpServletRequest request) {
        model.addAttribute("user", UserUtil.getSessionUser(request));
        model.addAttribute("show", showService.findById(showId));
        return "admin/editShow";
    }

    /**
     * Обрабатывает GET запрос, возвращает страницу
     * добавления нового сеанса.
     *
     * @param model модель
     * @param request запрос пользователя
     * @return страница добавления сеанса
     */
    @GetMapping("/addShow")
    public String addShow(Model model, HttpServletRequest request) {
        model.addAttribute("show", new Show());
        model.addAttribute("user", UserUtil.getSessionUser(request));
        return "admin/addShow";
    }

    /**
     * Обрабатывает POST запрос, перенаправляет на страницу
     * списка сеансов для администратора. Сохраняет файл постера
     * в хранилище графических изображений.
     *
     * @param show сеанс
     * @param file постер сеанса
     * @return перенаправление на страницу списка сеансов для администратора
     */
    @PostMapping("/saveShow")
    public String saveShow(@ModelAttribute Show show,
                           @RequestParam("file") MultipartFile file) {
        showService.saveOrUpdate(show, file);
        return "redirect:/adminShows";
    }

    /**
     * Обрабатывает POST запрос, перенаправляет на страницу
     * списка сеансов для администратора. Удаляет сеанс по идентификатору.
     *
     * @param showId идентификатор сеанса
     * @return перенаправление на страницу списка сеансов для администратора
     */
    @PostMapping("/deleteShow")
    public String deleteShow(@RequestParam(value = "showId") int showId) {
        showService.deleteById(showId);
        return "redirect:/adminShows";
    }

    /**
     * Выполняет локальный (уровня контроллера) перехват исключений
     * IOException, в случае перехвата,
     * перенаправляет на соответствующую веб страницу с описанием ошибки.
     *
     * @param e перехваченное исключение
     * @return модель для передачи данных об исключении на веб страницу
     */
    @ExceptionHandler(value = {IOException.class})
    public ModelAndView ioExceptionHandler(Exception e) {
        log.error(e.getLocalizedMessage());
        return new ModelAndView("error/400", "errorMessage", e.getMessage());
    }
}