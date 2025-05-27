package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.entity.Comment;
import ru.yandex.practicum.repository.CommentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public List<Comment> getPostComments(Long postId) {
        return commentRepository.getPostComments(postId);
    }

    @Transactional
    public void save(Long postId, String text) {
        Comment comment = Comment.builder()
                .commentText(text)
                .postId(postId)
                .build();
        commentRepository.save(comment);
    }

    @Transactional
    public void edit(Long postId, Long id, String text) {
        Comment comment = Comment.builder()
                .postId(postId)
                .commentText(text)
                .id(id)
                .build();
        commentRepository.editById(id, comment);
    }

    @Transactional
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }
}
