package ru.yandex.practicum.model;

import lombok.*;

import java.awt.image.BufferedImage;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Post {
    @EqualsAndHashCode.Include
    private Long id;
    //private BufferedImage image;
    private byte[] image;
    private String title;
    private String text;
    private String tags;
    private int likesCount;
}
