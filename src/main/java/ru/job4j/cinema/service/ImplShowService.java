package ru.job4j.cinema.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cinema.model.Show;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.ShowRepository;
import ru.job4j.cinema.repository.TicketRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Реализация сервиса по работе с сеансами
 *
 * @author Alexander Emelyanov
 * @version 1.0
 * @see ru.job4j.cinema.service.ShowService
 */
@Slf4j
@Service
public class ImplShowService implements ShowService {

    /**
     * Абсолютный путь к папке для хранения изображений указывается
     * в конфигурационном файле application.properties
     */
    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Количество рядов в кинозале
     */
    @Value("${show.rows}")
    private int rows;

    /**
     * Количество мест в ряде кинозала
     */
    @Value("${show.cells}")
    private int cells;

    /**
     * Объект для доступа к методам ShowRepository
     */
    private final ShowRepository showRepository;

    /**
     * Объект для доступа к методам TicketRepository
     *
     * @see ru.job4j.cinema.repository.TicketRepository
     */
    private final TicketRepository ticketRepository;

    /**
     * Конструктор класса.
     *
     * @param showRepository   объект для доступа к методам слоя ShowRepository
     * @param ticketRepository объект для доступа к методам слоя TicketRepository
     */
    public ImplShowService(ShowRepository showRepository, TicketRepository ticketRepository) {
        this.showRepository = showRepository;
        this.ticketRepository = ticketRepository;
    }

    /**
     * Возвращает список всех сеансов.
     *
     * @return {@code List<Show>} - список всех сеансов
     */
    @Override
    public List<Show> findAll() {
        List<Show> shows = showRepository.findAll();
        shows.sort(Comparator.comparing(Show::getId));
        return shows;
    }

    /**
     * Выполняет поиск сеанс по идентификатору. При успешном нахождении возвращает
     * сеанс, иначе выбрасывает исключение.
     *
     * @param id идентификатор сеанса
     * @return сеанса при успешном нахождении
     * @throws NoSuchElementException если сеанс не найден
     */
    @Override
    public Show findById(int id) {
        return showRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException(
                        String.format("Сеанс c id = %d не найден", id)));
    }

    /**
     * Выполняет сохранение сеанса и файла постера при добавлении сеанса
     * или редактирования. Вызывает соответствующие методы класса в зависимости от
     * того, нулевой идентификатор сеанса или нет.
     *
     * @param show сохраняемый сеанс
     * @param file сохраняемый сеанс
     * @return сеанс при успешном сохранении
     */
    @Override
    public Show saveOrUpdate(Show show, MultipartFile file) {
        String resultFilename = getFileName(file);
        show.setPosterName(resultFilename);
        Show showFromDB = null;
        if (show.getId() == 0) {
            showFromDB = save(show);
        } else {
            update(show);
        }
        return showFromDB;
    }

    /**
     * Выполняет сохранение сеанса при добавлении сеанса.
     * При успешном сохранении возвращает сохраненный сеанс,
     * иначе выбрасывается исключение.
     *
     * @param show сохраняемый сеанс
     * @return сеанс при успешном сохранении
     * @throws IllegalArgumentException если сохранение сеанса не произошло
     */
    @Override
    public Show save(Show show) {
        show = showRepository.save(show).orElseThrow(
                () -> new IllegalArgumentException("Сеанс не сохранен"));
        return show;
    }

    /**
     * Выполняет обновление сеанса. Выполняется сравнение постеров по имени,
     * Если постер изменен старый удаляется.
     *
     * @param show обновляемый сеанс
     * @throws NoSuchElementException если сеанс не найден
     */
    @Override
    public boolean update(Show show) {
        Show showFromDB = findById(show.getId());
        if (!showFromDB.getPosterName().equals(show.getPosterName())) {
            deletePosterByShowId(showFromDB.getId());
        }
        if (!showRepository.update(show)) {
            throw new NoSuchElementException(
                    String.format("Сеанс c id = %d не найден", show.getId()));
        }
        return true;
    }

    /**
     * Выполняет удаление сеанса по идентификатору. При успешном удалении
     * сеанса возвращает true, иначе выбрасывается исключение.
     * При удалении сеанса происходит и удаление постера к сеансу.
     *
     * @param id идентификатор сеанса
     * @return true при успешном удалении
     * @throws NoSuchElementException если сеанс не найден
     */
    @Override
    public boolean deleteById(int id) {
        if (uploadPath != null) {
            deletePosterByShowId(id);
        }
        boolean result = showRepository.deleteById(id);
        if (!result) {
            throw new NoSuchElementException(
                    String.format("Сеанс c id = %d не найден", id));
        }
        return true;
    }

    /**
     * Выполняет расчет списка рядов в зале со свободными местами
     * по переданному идентификатору сеанса. Для расчета используется
     * вспомогательный метод {@link ImplShowService#getFreeTicketMap(int)}.
     * Возвращает список рядов со свободными местами.
     *
     * @param id идентификатор сеанса
     * @return {@code List<Integer>} список рядов в зале со
     * свободными местами
     */
    @Override
    public List<Integer> getRows(int id) {
        Map<Integer, List<Integer>> freeTickets = getFreeTicketMap(id);
        List<Integer> rows = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> entry : freeTickets.entrySet()) {
            if (entry.getValue().size() > 0) {
                rows.add(entry.getKey());
            }
        }
        return rows;
    }

    /**
     * Выполняет расчет списка свободных мест в ряде по
     * идентификаторам сеанса и номеру ряда.
     * Для расчета используется вспомогательный метод
     * {@link ImplShowService#getFreeTicketMap(int)}.
     * Возвращает список свободных мест в ряде.
     *
     * @param id     идентификатор сеанса
     * @param posRow номер ряда
     * @return {@code List<Integer>} - список свободных мест в ряде
     */
    @Override
    public List<Integer> getCells(int id, int posRow) {
        Map<Integer, List<Integer>> freeTickets = getFreeTicketMap(id);
        return freeTickets.get(posRow);
    }

    /**
     * Возвращает сгенерированное отображение свободных мест в сеансе
     * по идентификатору.
     *
     * @param id идентификатор сеанса
     * @return {@code Map<Integer, List<Integer>>} - отображение, где ключ - номер ряда,
     * список - номера мест в ряде
     */
    private Map<Integer, List<Integer>> getFreeTicketMap(int id) {
        Map<Integer, List<Integer>> freeTickets = new HashMap<>();
        for (int i = 1; i <= rows; i++) {
            freeTickets.put(i, new ArrayList<>());
            for (int j = 1; j <= cells; j++) {
                freeTickets.get(i).add(j);
            }
        }
        List<Ticket> tickets = ticketRepository.findAllTicketsByShowId(id);
        for (Ticket ticket : tickets) {
            freeTickets.get(ticket.getPosRow()).remove(ticket.getCell() - 1);
        }
        return freeTickets;
    }

    /**
     * Удаляет постер из служебного каталога хранилища. Если удаление прошло удачно
     * вернет true, если в процессе удаление выброшено IOException false.
     *
     * @param id идентификатор сеанса
     * @return true если удаление
     */
    private boolean deletePosterByShowId(int id) {
        Show showFromDB = showRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException(
                        String.format("Сеанс c id = %d не найден", id)));
        if (showFromDB.getPosterName() != null) {
            String posterName = showFromDB.getPosterName();
            String fileName = uploadPath.substring(1) + "/" + posterName;
            try {
                Files.delete(Paths.get(fileName));
            } catch (IOException e) {
                log.error(e.getLocalizedMessage(), "Постер не удален");
                return false;
            }
        }
        return true;
    }

    /**
     * Возвращает имя файла, если файл пустой то вернет null
     * и копирует файл в директорию uploads, если он загружался.
     *
     * @param file файл постера
     * @return имя файлы постера сеанса
     */
    private String getFileName(MultipartFile file) {
        String result = "";
        if (!file.isEmpty() && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            result = uuidFile + "." + file.getOriginalFilename();
            try {
                file.transferTo(new File(uploadPath + "/" + result));
            } catch (IOException e) {
                log.error(e.getLocalizedMessage());
            }

        }
        return result;
    }
}
