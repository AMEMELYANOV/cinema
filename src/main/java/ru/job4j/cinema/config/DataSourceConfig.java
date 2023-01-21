package ru.job4j.cinema.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Конфигурация источника данных
 * @author Alexander Emelyanov
 * @version 1.0
 */
@Configuration
public class DataSourceConfig {

    /**
     * Выполняется создание объекта, содержащего параметры
     * подключения приложения к базе данных приложения,
     * параметры считываются из файла /resources/db.properties
     *
     * @return Properties - объект содержащий конфигурацию подключения к базе данных
     */
    private Properties loadDbProperties() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new InputStreamReader(
                        DataSourceConfig.class.getClassLoader()
                                .getResourceAsStream("db.properties")
                )
        )) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return cfg;
    }

    /**
     * Выполняется создание Bean BasicDataSource
     * содержащего pool соединений для подключения к СУБД
     *
     * @return BasicDataSource - объект для организации работы с базой данных
     */
    @Bean
    public BasicDataSource loadPool() {
        Properties cfg = loadDbProperties();
        BasicDataSource pool = new BasicDataSource();
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
        return pool;
    }
}
