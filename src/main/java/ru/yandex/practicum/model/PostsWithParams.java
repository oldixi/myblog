package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.dto.PostDto;

import java.util.List;

@Data
@AllArgsConstructor
public class PostsWithParams {
    List<PostDto> posts;
    private String search;
    private PagingParameters paging;
}
