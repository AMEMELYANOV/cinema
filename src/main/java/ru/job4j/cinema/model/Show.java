package ru.job4j.cinema.model;

import lombok.*;

/**
 * Модель данных сеансы
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
public class Show {
    /**
     * Идентификатор сеанса
     */
    @EqualsAndHashCode.Include
    private int id;

    /**
     * Название сеанса
     */
    private String name;

    /**
     * Подробное описание сеанса
     */
    private String description;

    /**
     * Постер сеанса
     */
    private String posterName;
}
