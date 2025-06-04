package ru.yandex.practicum.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.dto.CommentDto;
import ru.yandex.practicum.model.entity.Comment;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentMapper {
    private final ModelMapper mapper;
    public CommentDto toCommentDto(Comment comment) {
        return mapper.map(comment, CommentDto.class);
    }

    public List<CommentDto> toCommentsDto(List<Comment> comments) {
        return comments.stream().map(this::toCommentDto).toList();
    }

    public Comment toComment(CommentDto commentDto) {
        return mapper.map(commentDto, Comment.class);
    }
}
