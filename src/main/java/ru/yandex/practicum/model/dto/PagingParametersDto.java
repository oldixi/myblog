package ru.yandex.practicum.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PagingParametersDto {
    private int pageNumber;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
}
