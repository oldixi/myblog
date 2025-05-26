package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.repository.CommentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public List<Comment> getPostComments(Long postId) {
        return commentRepository.getPostComments(postId);
    }

    public void save(Long postId, String text) {
        Comment comment = Comment.builder()
                .commentText(text)
                .postId(postId)
                .build();
        commentRepository.save(comment);
    }

    public void edit(Long postId, Long id, String text) {
        Comment comment = Comment.builder()
                .postId(postId)
                .id(id)
                .build();
        commentRepository.editById(id, comment);
    }

    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }
}
