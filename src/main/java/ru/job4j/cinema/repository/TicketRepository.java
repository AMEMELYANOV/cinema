package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.Ticket;

import java.util.List;
import java.util.Optional;

/**
 * Хранилище билетов
 * @see ru.job4j.cinema.model.Ticket
 * @author Alexander Emelyanov
 * @version 1.0
 */
public interface TicketRepository {

    /**
     * Возвращает список всех билетов
     *
     * @return список всех билетов
     */
    List<Ticket> findAll();

    /**
     * Выполняет поиск билета по идентификатору. При успешном нахождении возвращает
     * Optional с объектом билета. Иначе возвращает Optional.empty().
     *
     * @param id идентификатор билета
     * @return Optional.of(ticket) при успешном нахождении, иначе Optional.empty()
     */
    Optional<Ticket> findById(int id);

    /**
     * Выполняет сохранение билета. При успешном сохранении возвращает Optional с
     * объектом билета, у которого проинициализировано id. Иначе возвращает Optional.empty()
     * Сохранение не произойдет, если уникальный набор из show, pos_row и cell использовались
     * при сохранении в другом билете.
     *
     * @param ticket сохраняемый билет
     * @return Optional.of(ticket) при успешном сохранении, иначе Optional.empty()
     */
    Optional<Ticket> save(Ticket ticket);

    /**
     * Выполняет обновление билета.
     *
     * @param ticket объект билета
     * @return true при успешном обновлении билета, иначе false
     */
    boolean update(Ticket ticket);

    /**
     * Выполняет удаление билета по идентификатору. При успешном
     * удалении возвращает true, при неудачном false.
     *
     * @param id идентификатор билета
     * @return true при успешном удалении билета, иначе false
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

    /**
     * Возвращает список всех билетов по идентификатору сеанса
     *
     * @param id идентификатор сеанса
     * @return список всех билетов
     */
    List<Ticket> findAllTicketsByShowId(int id);
}
