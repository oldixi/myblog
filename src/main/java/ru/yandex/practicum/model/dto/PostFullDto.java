package ru.yandex.practicum.model.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PostFullDto {
    private Long id;
    private String title;
    private List<String> text;
    private String imagePath;
    private int likesCount;
    private List<String> tags;
    private List<CommentDto> comments;
    public String getTextPreview() {
        if (text == null || text.isEmpty()) return null;
        return text.get(0);
    }
}
