package ru.yandex.practicum.configuration;

import org.springframework.context.annotation.Bean;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfiguration {
    @Bean()
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
