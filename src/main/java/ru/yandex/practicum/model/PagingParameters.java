package ru.yandex.practicum.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PagingParameters {
    private int pageNumber;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
}
