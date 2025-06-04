package ru.yandex.practicum.model.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PostDto {
    private Long id;
    private String title;
    private String text;
    private MultipartFile image;
    private String tags;
}
