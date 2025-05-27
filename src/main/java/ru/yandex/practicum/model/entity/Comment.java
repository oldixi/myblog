package ru.yandex.practicum.model.entity;

import lombok.*;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Comment {
    @EqualsAndHashCode.Include
    private Long id;
    private String commentText;
    private Long postId;
}
