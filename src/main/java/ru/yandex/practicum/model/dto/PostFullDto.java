package ru.yandex.practicum.model.dto;

import lombok.*;
import ru.yandex.practicum.model.entity.Comment;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PostFullDto {
    @EqualsAndHashCode.Include
    private Long id;
    private String title;
    private String textPreview;
    //private String imagePath;
    private int likesCount;
    private String tags;
    private List<Comment> comments  = new ArrayList<>();
}
