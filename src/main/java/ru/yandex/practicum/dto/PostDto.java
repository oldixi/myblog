package ru.yandex.practicum.dto;

import lombok.*;
import ru.yandex.practicum.model.Comment;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PostDto {
    @EqualsAndHashCode.Include
    private Long id;
    private String title;
    private String text;
    private String imagePath;
    private int likesCount;
    private String tags;
    private List<Comment> comments  = new ArrayList<>();
}
