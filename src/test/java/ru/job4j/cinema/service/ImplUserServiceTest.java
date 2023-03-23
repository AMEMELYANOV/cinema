package ru.job4j.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.JdbcShowRepository;
import ru.job4j.cinema.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doReturn;

/**
 * Тест класс реализации сервисного слоя пользователей
 *
 * @author Alexander Emelyanov
 * @version 1.0
 * @see JdbcShowRepository
 */
class ImplUserServiceTest {

    /**
     * Объект для доступа к методам UserRepository
     */
    private UserRepository userRepository;

    /**
     * Объект для доступа к методам ImplUserService
     */
    private ImplUserService userService;

    /**
     * Пользователь
     */
    private User user;

    /**
     * Создает необходимые для выполнения тестов общие объекты.
     * Создание выполняется перед каждым тестом.
     */
    @BeforeEach
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new ImplUserService(userRepository);
        user = User.builder()
                .id(1)
                .username("user")
                .email("user@mail.ru")
                .password("123")
                .phone("+79000000000")
                .build();
    }

    /**
     * Выполняется проверка возвращения списка пользователей
     * от userRepository, если в списке есть элементы.
     */
    @Test
    void whenFindAllThenReturnList() {
        User user1 = User.builder()
                .id(2)
                .username("user1")
                .email("user1@mail.ru")
                .password("123")
                .phone("+79000000001")
                .build();
        List<User> users = List.of(user, user1);
        doReturn(users).when(userRepository).findAll();
        List<User> userList = userService.findAll();

        assertThat(userList).isNotNull();
        assertThat(userList.size()).isEqualTo(2);
    }

    /**
     * Выполняется проверка возвращения списка пользователей
     * от userRepository, если список пустой.
     */
    @Test
    void whenFindAllThenReturnEmptyList() {
        doReturn(Collections.emptyList()).when(userRepository).findAll();
        List<User> userList = userService.findAll();

        assertThat(userList).isEmpty();
    }

    /**
     * Выполняется проверка возвращения пользователя, при возврате
     * от userRepository Optional.of(user), т.е. если пользователь найден по идентификатору.
     */
    @Test
    void whenFindByIdThenReturnUser() {
        doReturn(Optional.of(user)).when(userRepository).findById(1);
        User userFromDB = userService.findById(1);

        assertThat(userFromDB).isEqualTo(user);
        assertThat(userFromDB).isNotNull();
    }

    /**
     * Выполняется проверка выброса исключения, при возврате от
     * userRepository Optional.empty(), если пользователь не найден по идентификатору.
     */
    @Test
    void whenFindByIdThenThrowsException() {
        doReturn(Optional.empty()).when(userRepository).findById(anyInt());

        assertThrows(NoSuchElementException.class, () -> userService.findById(anyInt()));
    }

    /**
     * Выполняется проверка возврата пользователя, при возврате от
     * userRepository Optional.of(user), т.е. если пользователь был сохранен.
     */
    @Test
    void whenSaveThenReturnUser() {
        doReturn(Optional.of(user)).when(userRepository).save(user);
        User userFromDB = userService.save(user);

        assertThat(userFromDB).isEqualTo(user);
        assertThat(userFromDB).isNotNull();
    }

    /**
     * Выполняется проверка выброса исключения, при возврате от
     * userRepository Optional.empty(), если пользователь не был сохранен.
     */
    @Test
    void whenSaveThenThrowsException() {
        doReturn(Optional.empty()).when(userRepository).save(user);

        assertThrows(IllegalArgumentException.class, () -> userService.save(user));
    }

    /**
     * Выполняется проверка обновления пользователя, при возврате от
     * userRepository true, т.е. если пользователь был сохранен.
     */
    @Test
    void whenUpdateThenReturnTrue() {
        doReturn(true).when(userRepository).update(user);
        boolean result = userService.update(user);

        assertThat(result).isEqualTo(true);
    }

    /**
     * Выполняется проверка выброса исключения, при возврате от
     * userRepository false, т.е. если пользователь не был обновлен.
     */
    @Test
    void whenUpdateThenThrowsException() {
        doReturn(false).when(userRepository).update(user);

        assertThrows(IllegalArgumentException.class, () -> userService.update(user));
    }

    /**
     * Выполняется проверка удаления пользователя, при возврате
     * от userRepository true, т.е. если пользователь удален по идентификатору.
     */
    @Test
    void whenDeleteByIdThenReturnTrue() {
        doReturn(true).when(userRepository).deleteById(anyInt());
        boolean result = userService.deleteById(anyInt());

        assertThat(result).isEqualTo(true);
    }

    /**
     * Выполняется проверка выброса исключения, при возврате
     * от userRepository false, т.е. если пользователь не удален или
     * не найден по идентификатору.
     */
    @Test
    void whenDeleteByIdThenThrowsException() {
        doReturn(false).when(userRepository).deleteById(anyInt());

        assertThrows(NoSuchElementException.class, () -> userService.deleteById(anyInt()));
    }

    /**
     * Выполняется проверка возвращения пользователя, при возврате
     * от userRepository Optional.of(user), т.е. если пользователь найден по email.
     */
    @Test
    void whenFindUserByEmailThenReturnUser() {
        doReturn(Optional.of(user)).when(userRepository).findUserByEmail(user.getEmail());
        User userFromDB = userService.findUserByEmail(user.getEmail());

        assertThat(userFromDB).isEqualTo(user);
    }

    /**
     * Выполняется проверка выброса исключения, при возврате от
     * userRepository Optional.empty(), если пользователь не найден по email.
     */
    @Test
    void whenFindUserByEmailThenThrowsException() {
        doReturn(Optional.empty()).when(userRepository).findUserByEmail(user.getEmail());

        assertThrows(NoSuchElementException.class,
                () -> userService.findUserByEmail(user.getEmail()));
    }

    /**
     * Выполняется проверка валидации пользователя при входе по email и паролю, при возврате
     * от userRepository user и совпадении пароля, производится возврат пользователя
     * из метода.
     */
    @Test
    void whenValidateUserLoginThenReturnUser() {
        doReturn(Optional.of(user)).when(userRepository).findUserByEmail(user.getEmail());
        User userFromDB = userService.validateUserLogin(user);

        assertThat(userFromDB).isEqualTo(user);
    }

    /**
     * Выполняется проверка валидации пользователя по email и паролю, введенных на
     * форме входа, при возврате от userRepository user и не совпадении пароля,
     * производится выброс исключения.
     */
    @Test
    void whenValidateUserLoginAndPasswordNotEqualThenThrowsException() {
        doReturn(Optional.of(user)).when(userRepository).findUserByEmail(user.getEmail());
        User newUser = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password("1234")
                .phone(user.getPhone())
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> userService.validateUserLogin(newUser));
    }
}