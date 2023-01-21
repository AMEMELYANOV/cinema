package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.Show;

import java.util.List;
import java.util.Optional;

/**
 * Хранилище сеансов
 * @see ru.job4j.cinema.model.Show
 * @author Alexander Emelyanov
 * @version 1.0
 */
public interface ShowRepository {

    /**
     * Возвращает список всех сеансов
     *
     * @return список всех сеансов
     */
    List<Show> findAll();

    /**
     * Выполняет поиск сеанса по идентификатору. При успешном нахождении возвращает
     * Optional с объектом сеанса. Иначе возвращает Optional.empty().
     *
     * @param id идентификатор сеанса
     * @return Optional.of(show) при успешном нахождении, иначе Optional.empty()
     */
    Optional<Show> findById(int id);

    /**
     * Выполняет сохранение сеанса. При успешном сохранении возвращает Optional с
     * объектом сеанса, у которого проинициализировано id. Иначе возвращает Optional.empty()
     *
     * @param show сохраняемый сеанс
     * @return Optional.of(show) при успешном сохранении, иначе Optional.empty()
     */
    Optional<Show> save(Show show);

    /**
     * Выполняет обновление объекта сеанс.
     *
     * @param show объект сеанс
     * @return true при успешном обновлении сеанса, иначе false
     */
    boolean update(Show show);

    /**
     * Выполняет удаление сеанса по идентификатору. При успешном
     * удалении возвращает true, при неудачном false.
     *
     * @param id идентификатор сеанса
     * @return true при успешном удалении сеанса, иначе false
     */
    boolean deleteById(int id);
}
