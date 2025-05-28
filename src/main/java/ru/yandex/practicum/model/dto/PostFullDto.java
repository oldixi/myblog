package ru.yandex.practicum.model.dto;

import lombok.*;
import ru.yandex.practicum.model.entity.Comment;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PostFullDto {
    private Long id;
    private String title;
    private String textPreview;
    //private String imagePath;
    private int likesCount;
    private String tags;
    private List<Comment> comments  = new ArrayList<>();
}
