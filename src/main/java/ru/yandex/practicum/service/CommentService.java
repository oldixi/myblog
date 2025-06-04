package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mapper.CommentMapper;
import ru.yandex.practicum.model.dto.CommentDto;
import ru.yandex.practicum.model.entity.Comment;
import ru.yandex.practicum.repository.CommentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public List<CommentDto> getPostComments(Long postId) {
        return commentMapper.toCommentsDto(commentRepository.getByPostId(postId));
    }

    @Transactional
    public Comment save(Long postId, String text) {
        CommentDto comment = CommentDto.builder()
                .text(text)
                .postId(postId)
                .build();
        return commentRepository.save(commentMapper.toComment(comment));
    }

    @Transactional
    public Comment edit(Long postId, Long id, String text) {
        CommentDto comment = CommentDto.builder()
                .postId(postId)
                .text(text)
                .id(id)
                .build();
        return commentRepository.save(commentMapper.toComment(comment));
    }

    @Transactional
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }
}
