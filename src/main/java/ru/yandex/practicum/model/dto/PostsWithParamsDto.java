package ru.yandex.practicum.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class PostsWithParamsDto {
    List<PostFullDto> posts;
    private String search;
    private PagingParametersDto paging;
}
