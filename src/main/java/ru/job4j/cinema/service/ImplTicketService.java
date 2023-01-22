package ru.job4j.cinema.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.repository.TicketRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Реализация сервиса по работе с билета
 * @see ru.job4j.cinema.service.TicketService
 * @author Alexander Emelyanov
 * @version 1.0
 */
@AllArgsConstructor
@Service
public class ImplTicketService implements TicketService {

    /**
     * Объект для доступа к методам TicketRepository
     */
    private final TicketRepository ticketRepository;

    /**
     * Возвращает список всех билетов
     *
     * @return {@code List<Ticket>} - список всех билетов
     */
    @Override
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    /**
     * Выполняет поиск билета по идентификатору. При успешном нахождении возвращает
     * билет, иначе выбрасывает исключение.
     *
     * @param id идентификатор билета
     * @return билет при успешном нахождении
     * @exception NoSuchElementException, если билет не найден
     */
    @Override
    public Ticket findById(int id) {
        return ticketRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException(
                        String.format("Билет c id = %d не найден", id)));
    }

    /**
     * Выполняет сохранение билета. При успешном сохранении возвращает
     * сохраненный билет, иначе выбрасывается исключение.
     *
     * @param ticket сохраняемый билет
     * @return билет при успешном сохранении
     * @exception IllegalArgumentException, если сохранение билета не произошло
     */
    @Override
    public Ticket save(Ticket ticket) {
        Optional<Ticket> optionalTicket = ticketRepository.save(ticket);
        return optionalTicket.orElseThrow(() -> new IllegalArgumentException("Билет уже продан"));
    }

    /**
     * Выполняет обновление билета.
     *
     * @param ticket обновляемый билет
     * @exception NoSuchElementException, если билет не найден
     */
    @Override
    public boolean update(Ticket ticket) {
        if (!ticketRepository.update(ticket)) {
            throw new NoSuchElementException(
                    String.format("Билет c id = %d не найден", ticket.getId()));
        }
        return true;
    }

    /**
     * Выполняет удаление билета по идентификатору. При успешном удалении
     * билета возвращает true, иначе выбрасывается исключение.
     *
     * @param id идентификатор билета
     * @return true при успешном удалении
     * @exception NoSuchElementException, если билет не найден
     */
    @Override
    public boolean deleteById(int id) {
        if (!ticketRepository.deleteById(id)) {
            throw new NoSuchElementException(
                    String.format("Билет c id = %d не найден", id));
        }
        return true;
    }

    /**
     * Выполняет удаление билетов по идентификатору сеанса. При успешном удалении
     * билетов возвращает true, иначе выбрасывается исключение.
     *
     * @param id идентификатор сеанса
     * @return true при успешном удалении
     * @exception NoSuchElementException, если билет не найден
     */
    @Override
    public boolean deleteTicketsByShowId(int id) {
        if (!ticketRepository.deleteTicketsByShowId(id)) {
            throw new NoSuchElementException(
                    String.format("Билеты у сеанса c id = %d не найдены", id));
        }
        return true;
    }
}
