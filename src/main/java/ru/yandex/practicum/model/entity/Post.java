package ru.yandex.practicum.model.entity;

import lombok.*;

import java.awt.image.BufferedImage;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Post {
    private Long id;
    private byte[] image;
    private String title;
    private String text;
    private String tags;
    private int likesCount;
}
