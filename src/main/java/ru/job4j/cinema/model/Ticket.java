package ru.job4j.cinema.model;

import lombok.*;

/**
 * Модель данных билеты
 * @author Alexander Emelyanov
 * @version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ticket {
    /**
     * Идентификатор билета
     */
    @EqualsAndHashCode.Include
    private int id;

    /**
     * Сеанс билета
     */
    private Show show;

    /**
     * Номер ряда билета
     */
    private int posRow;

    /**
     * Номер кресла билета
     */
    private int cell;

    /**
     * Пользователь билета
     */
    private User user;
}
