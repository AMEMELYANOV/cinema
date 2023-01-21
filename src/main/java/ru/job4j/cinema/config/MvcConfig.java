package ru.job4j.cinema.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Веб конфигурация для работы с изображениями
 * @author Alexander Emelyanov
 * @version 1.0
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * Абсолютный путь к папке для хранения изображений указывается
     * в конфигурационном файле application.properties
     */
    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Выполняет регистрацию обработчика работы с изображениями
     *
     * @param registry регистратор ресурсов
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file://" + uploadPath + "/");
    }
}
