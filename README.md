# job4j_cinema

# **Проект Кинотеатр**

## <p id="contents">Оглавление</p>

<ul>
<li><a href="#01">Описание проекта</a></li>
<li><a href="#02">Стек технологий</a></li>
<li><a href="#03">Требования к окружению</a></li>
<li><a href="#04">Сборка и запуск проекта</a>
    <ol type="1">
        <li><a href="#0401">Сборка проекта</a></li>
        <li><a href="#0402">Запуск проекта</a></li>
    </ol>
</li>
<li><a href="#05">Взаимодействие с приложением</a>
    <ol  type="1">
        <li><a href="#0501">Страница приветствия</a></li>
        <li><a href="#0502">Страница регистрации</a></li>
        <li><a href="#0503">Страница входа</a></li>
        <li><a href="#0504">Список киносеансов</a></li>
        <li><a href="#0505">Покупка билетов</a></li>
        <li><a href="#0506">Управление списком киносеансов</a></li>
        <li><a href="#0507">Добавление киносеанса</a></li>
        <li><a href="#0508">Редактирование профиля</a></li>
        <li><a href="#0509">Выход из приложения</a></li>
    </ol>
</li>
<li><a href="#contacts">Контакты</a></li>
</ul>

## <p id="01">Описание проекта

MVC приложение реализующее сервис по продаже билетов в кинотеатр.
В приложении реализована регистрация пользователей, учет
купленных билетов. Реализованы функции администратора: добавление,
редактирование, удаление киносеансов.

<p><a href="#contents">К оглавлению</a></p>

## <p id="02">Стек технологий

- Java 17
- PostgreSQL 14, JDBC, Liquibase
- Maven 3.8
- Spring Boot 2.6
- HTML, Bootstrap, Thymeleaf
- JUnit 5

Инструменты:

- Javadoc, JaCoCo, Checkstyle

<p><a href="#contents">К оглавлению</a></p>

## <p id="03">Требования к окружению

Java 17, Maven 3.8, PostgreSQL 14

<p><a href="#contents">К оглавлению</a></p>

## <p id="04">Сборка и запуск проекта

Для выполнения действий данного раздела необходимо установить
и настроить систему сборки проектов Maven.

### <p id="0401">1. Сборка проекта</p>

Команда для сборки в jar:
`mvn clean package -DskipTests`

<p><a href="#contents">К оглавлению</a></p>

### <p id="0402">2. Запуск проекта</p>

Перед запуском проекта необходимо создать базу данных cinema
в PostgreSQL, команда для создания базы данных:
`create database cinema;`
Средство миграции Liquibase автоматически создаст структуру
базы данных и наполнит ее предустановленными данными.
Команда для запуска приложения:
`mvn spring-boot:run`

При создании таблиц средством миграции Liquibase, в базе данных
сохраняется пользователь с правами администратора.
**login (аккаунт) - admin@cinema.ru, password - 1111.** и несколько
киносеансов в качестве демонстрации, при необходимости их можно
удалить и добавить свои.

<p><a href="#contents">К оглавлению</a></p>

## <p id="05">Взаимодействие с приложением</p>

Локальный доступ к приложению осуществляется через любой современный браузер
по адресу `http://localhost:8080/`

### <p id="0501">1. Страница приветствия

Со страницы приветствия пользователь может перейти к регистрации,
либо к странице входа.

![alt text](img/cinema_img_01.png)

<p><a href="#contents">К оглавлению</a></p>

### <p id="0502">2. Страница регистрации

На странице регистрации пользователю необходимо заполнить поля:
Имя, электронная почта, дважды ввести пароль и телефонный номер.

![alt text](img/cinema_img_02.png)

При несоблюдении требований к данных формы, на странице регистрации
будут отражены замечания.

![alt text](img/cinema_img_02_1.png)

<p><a href="#contents">К оглавлению</a></p>

### <p id="0503">3. Страница входа

На странице входа необходимо указать адрес электронной почты и
ввести свой пароль.

![alt text](img/cinema_img_03.png)

При неправильных учетных данных, об этом будет выведено
сообщение на странице входа.

![alt text](img/cinema_img_03_1.png)

<p><a href="#contents">К оглавлению</a></p>

### <p id="0504">4. Список киносеансов

После входа в приложение отображается главная страница со
списком киносеансов.
![alt text](img/cinema_img_04.png)

Если пользователь является администратором,
то в панели навигации отображаются дополнительные ссылки,
доступные только администратору.

![alt text](img/cinema_img_04_1.png)

<p><a href="#contents">К оглавлению</a></p>

### <p id="0505">5. Покупка билетов

При нажатии на кнопку "Купить" на странице списка киносеансов,
выполняется переход на страницу выбора ряда.

![alt text](img/cinema_img_05.png)

Далее необходимо выбрать место в ряду на странице выбора места.

![alt text](img/cinema_img_06.png)

После перехода к покупке, пользователю предлагается проверить
выбранные параметры билета и подтвердить или отменить покупку.

![alt text](img/cinema_img_06_1.png)

При подтверждении покупки и отсутствии коллизий (одновременной покупкой
с другими пользователями) выполняется переход на информационную страницу
с сообщением, что билет уже продан.

![alt text](img/cinema_img_06_2.png)

При возникновении коллизии пользователь будет перенаправлен на
страницу с ошибкой.

![alt text](img/cinema_img_06_3.png)

<p><a href="#contents">К оглавлению</a></p>

### <p id="0506">6. Управление списком киносеансов

Страница доступна только пользователю с администраторскими правами,
позволяет: очищать списки купленных билетов, удалять и редактировать
киносеансы.

![alt text](img/cinema_img_07.png)

<p><a href="#contents">К оглавлению</a></p>

### <p id="0507">7. Добавление киносеанса

Страница добавления киносеанса, необходимо заполнить необходимые поля
и нажать кнопку "Сохранить", для добавления нового киносеанса в приложение.

![alt text](img/cinema_img_08.png)

<p><a href="#contents">К оглавлению</a></p>

### <p id="0508">8. Редактирование профиля

На странице редактирования профиля можно изменить: имя, пароль, номер телефона.

![alt text](img/cinema_img_09.png)

Проверка новых значений данных пользователя происходит по правилам регистрации
и аналогичным выводом ошибок заполнения в соответствующие поля.

![alt text](img/cinema_img_09_1.png)

<p><a href="#contents">К оглавлению</a></p>

### <p id="0509">9. Выход из приложения

При нажатии в панели навигации на ссылку "Выход", происходит
выход пользователя из приложения с перенаправлением на страницу входа и
сообщением о том, что пользователь вышел. При выходе сессия в которой работал
пользователь удаляется.

![alt text](img/cinema_img_10.png)

<p><a href="#contents">К оглавлению</a></p>

## <p id="contacts">Контакты</p>

[![alt-text](https://img.shields.io/badge/-telegram-grey?style=flat&logo=telegram&logoColor=white)](https://t.me/T_AlexME)
&nbsp;&nbsp;
[![alt-text](https://img.shields.io/badge/@%20email-005FED?style=flat&logo=mail&logoColor=white)](mailto:amemelyanov@yandex.ru)
&nbsp;&nbsp;

<p><a href="#contents">К оглавлению</a></p>