package ru.yandex.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ru.yandex.practicum.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    //List<Post> getPosts(String search, Pageable page);
    List<Post> getPosts(String search, int limit);
    Optional<Post> getById(Long id);
    void save(Post post);
    void deleteById(Long id);
    void editById(Long id, Post post);
}