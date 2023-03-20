package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import ru.job4j.cinema.model.Show;
import ru.job4j.cinema.repository.JdbcShowRepository;
import ru.job4j.cinema.repository.ShowRepository;
import ru.job4j.cinema.repository.TicketRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Тест класс реализации сервисного слоя сеансов
 * @see JdbcShowRepository
 * @author Alexander Emelyanov
 * @version 1.0
 */
class ImplShowServiceTest {

    /**
     * Объект для доступа к методам ShowRepository
     */
    private ShowRepository showRepository;

    /**
     * Объект для доступа к методам TicketRepository
     */
    private TicketRepository ticketRepository;

    /**
     * Объект для доступа к методам TicketController
     */
    private  ImplShowService showService;

    /**
     * Сеанс
     */
    private Show show;

    /**
     * Создает необходимые для выполнения тестов общие объекты.
     * Создание выполняется перед каждым тестом.
     */
    @BeforeEach
    public void setup() {
        showRepository = Mockito.mock(ShowRepository.class);
        ticketRepository = Mockito.mock(TicketRepository.class);
        showService = new ImplShowService(showRepository, ticketRepository);
        show = Show.builder()
                .id(0)
                .name("Show")
                .description("Description")
                .posterName("Poster")
                .build();
    }

    /**
     * Выполняется проверка возвращения списка сеансов
     * от showRepository, если в списке есть элементы.
     */
    @Test
    void whenFindAllThenReturnList() {
        Show show1 = Show.builder()
                .id(1)
                .name("Show1")
                .description("Description1")
                .posterName("Poster1")
                .build();
        List<Show> shows = new ArrayList<>();
        shows.add(show);
        shows.add(show1);
        doReturn(shows).when(showRepository).findAll();
        List<Show> showList = showService.findAll();

        assertThat(showList).isNotNull();
        assertThat(showList.size()).isEqualTo(2);
    }

    /**
     * Выполняется проверка возвращения списка сеансов
     * от showRepository, если список пустой.
     */
    @Test
    void whenFindAllThenReturnEmptyList() {
        doReturn(Collections.emptyList()).when(showRepository).findAll();
        List<Show> showList = showService.findAll();

        assertThat(showList).isEmpty();
        assertThat(showList.size()).isEqualTo(0);
    }

    /**
     * Выполняется проверка возвращения сеанса, при возврате
     * от showRepository Optional.of(show), т.е. если сеанс найден по идентификатору.
     */
    @Test
    void whenFindByIdThenReturnShow() {
        doReturn(Optional.of(show)).when(showRepository).findById(1);
        Show showFromDB = showService.findById(1);

        assertThat(showFromDB).isEqualTo(show);
        assertThat(showFromDB).isNotNull();
    }

    /**
     * Выполняется проверка выброса исключения, при возврате от
     * showRepository Optional.empty(), если сеанс не найден по идентификатору.
     */
    @Test
    void whenFindByIdThenThrowsException() {
        doReturn(Optional.empty()).when(showRepository).findById(anyInt());

        assertThrows(NoSuchElementException.class, () -> showService.findById(anyInt()));
    }

    /**
     * Выполняется проверка методы вызова save.
     */
    @Test
    void whenSaveOpUpdateThenPerformSave() {
        doReturn(Optional.of(show)).when(showRepository).save(show);
        MockMultipartFile file = new MockMultipartFile("poster", "",
                "text/plain", "filename.png".getBytes());
        showService.saveOrUpdate(show, file);
        verify(showRepository).save(show);
    }

    /**
     * Выполняется проверка методы вызова update.
     */
    @Test
    void whenSaveOpUpdateThenPerformUpdate() {
        Show show2 = Show.builder()
                .id(1)
                .name("Show")
                .description("Description")
                .posterName("Poster")
                .build();
        doReturn(true).when(showRepository).update(show2);
        doReturn(Optional.of(show2)).when(showRepository).findById(show2.getId());
        MockMultipartFile file = new MockMultipartFile("poster", "",
                "text/plain", "filename.png".getBytes());
        showService.saveOrUpdate(show2, file);
        verify(showRepository).update(show2);
    }

    /**
     * Выполняется проверка возврата сеанса, при возврате от
     * showRepository Optional.of(show), т.е. если сеанс был сохранен.
     */
    @Test
    void whenSaveThenReturnShow() {
        doReturn(Optional.of(show)).when(showRepository).save(show);
        Show showFromDB = showService.save(show);
        assertThat(showFromDB).isEqualTo(show);
        assertThat(showFromDB).isNotNull();
    }

    /**
     * Выполняется проверка выброса исключения, при возврате от
     * showRepository Optional.empty(), если сеанс не был сохранен.
     */
    @Test
    void whenSaveThenThrowsException() {
        doReturn(Optional.empty()).when(showRepository).save(show);

        assertThrows(IllegalArgumentException.class, () -> showService.save(show));
    }

    /**
     * Выполняется проверка обновление сеанса, при возврате от
     * showRepository true, т.е. если сеанс был сохранен.
     */
    @Test
    void whenUpdateThenReturnTrue() {
        doReturn(true).when(showRepository).update(show);
        doReturn(Optional.of(show)).when(showRepository).findById(show.getId());
        boolean result = showService.update(show);

        assertThat(result).isEqualTo(true);
    }

    /**
     * Выполняется проверка выброса исключения, при возврате от
     * showRepository false, т.е. если сеанс не был обновлен.
     */
    @Test
    void whenUpdateThenThrowsException() {
        doReturn(false).when(showRepository).update(show);

        assertThrows(NoSuchElementException.class, () -> showService.update(show));
    }

    /**
     * Выполняется проверка удаления сеанса, при возврате
     * от showRepository true, т.е. если сеанс удален по идентификатору.
     */
    @Test
    void whenDeleteByIdThenReturnTrue() {
        doReturn(true).when(showRepository).deleteById(anyInt());
        doReturn(Optional.of(show)).when(showRepository).findById(anyInt());
        boolean result = showService.deleteById(0);

        assertThat(result).isEqualTo(true);
    }

    /**
     * Выполняется проверка выброса исключения, при возврате
     * от showRepository false, т.е. если сеанс не удален
     * (не найден) по идентификатору.
     */
    @Test
    void whenDeleteByIdThenThrowsException() {
        doReturn(false).when(showRepository).deleteById(anyInt());

        assertThrows(NoSuchElementException.class, () -> showService.deleteById(anyInt()));
    }
}