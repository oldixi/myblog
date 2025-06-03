package ru.yandex.practicum.repository;

import ru.yandex.practicum.model.entity.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> getPostComments(Long postId);
    void save(Comment comment);
    void deleteById(Long id);
    void editById(Long id, Comment comment);
}
