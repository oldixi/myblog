package ru.yandex.practicum.configuration;

import jakarta.servlet.annotation.MultipartConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@MultipartConfig
@ComponentScan(basePackages = {"ru.yandex.practicum"})
@PropertySource("classpath:application.properties")
public class WebConfiguration {
    @Bean
    public StandardServletMultipartResolver multipartResolver(){
        return new StandardServletMultipartResolver();
    }
}
