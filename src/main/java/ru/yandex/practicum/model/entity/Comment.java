package ru.yandex.practicum.model.entity;

import lombok.*;

@Data
@Builder
@EqualsAndHashCode
public class Comment {
    private Long id;
    private String text;
    private Long postId;
}
