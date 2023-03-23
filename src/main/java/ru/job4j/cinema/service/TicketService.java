package ru.job4j.cinema.service;

import ru.job4j.cinema.model.Ticket;

import java.util.List;

/**
 * Сервис билетов, логика работы с билетами
 *
 * @author Alexander Emelyanov
 * @version 1.0
 * @see ru.job4j.cinema.model.Ticket
 */
public interface TicketService {

    /**
     * Возвращает список всех билетов
     *
     * @return {@code List<Ticket>} - список всех билетов
     */
    List<Ticket> findAll();

    /**
     * Выполняет поиск билета по идентификатору. При успешном нахождении возвращает
     * билет, иначе выбрасывает исключение.
     *
     * @param id идентификатор билета
     * @return билет при успешном нахождении
     */
    Ticket findById(int id);

    /**
     * Выполняет сохранение билета. При успешном сохранении возвращает
     * сохраненный билет, иначе выбрасывается исключение.
     *
     * @param ticket сохраняемый билет
     * @return билет при успешном сохранении
     */
    Ticket save(Ticket ticket);

    /**
     * Выполняет обновление билета.
     *
     * @param ticket обновляемый билет
     * @return true при успешном обновлении билета
     */
    boolean update(Ticket ticket);

    /**
     * Выполняет удаление билета по идентификатору. При успешном удалении
     * билета возвращает true, иначе выбрасывается исключение.
     *
     * @param id идентификатор билета
     * @return true при успешном обновлении билета
     */
    boolean deleteById(int id);

    /**
     * Выполняет удаление билетов по идентификатору сеанса. При успешном
     * удалении возвращает true, при неудачном false.
     *
     * @param id идентификатор сеанса
     * @return true при успешном удалении билетов, иначе false
     */
    boolean deleteTicketsByShowId(int id);

}
