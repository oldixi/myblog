package ru.yandex.practicum.repository;

import ru.yandex.practicum.model.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    List<Post> getPosts(String search, int limit, int offset);
    Optional<Post> getById(Long id);
    Long save(Post post);
    void deleteById(Long id);
    void editById(Long id, Post post);
    void likeById(Long id, int likeCount);
    int getPostsCount();
}