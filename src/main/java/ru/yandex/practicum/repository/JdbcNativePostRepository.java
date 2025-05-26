package ru.yandex.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Post;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcNativePostRepository implements PostRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Post> getPosts(String search, int limit) {
        return jdbcTemplate.query(
                "select id, name, post_text, tags, likes_count, picture from post where tags like '%?%' order by id desc limit ?",
                (rs, rowNum) -> {
                    try {
                        return Post.builder()
                                .id(rs.getLong("id"))
                                .title(rs.getString("name"))
                                .text(rs.getString("post_text"))
                                .tags(rs.getString("tags"))
                                .likesCount(rs.getInt("likes_count"))
                                .image(ImageIO.read(rs.getBlob("picture").getBinaryStream()))
                                .build();
                    } catch (IOException e) {
                        return Post.builder()
                                .id(rs.getLong("id"))
                                .title(rs.getString("name"))
                                .text(rs.getString("post_text"))
                                .tags(rs.getString("tags"))
                                .likesCount(rs.getInt("likes_count"))
                                .build();
                    }
                }, search, limit);
    }

    @Override
    public Optional<Post> getById(Long id) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    "select id, name, post_text, tags, likes_count, picture from post where id=?",
                    (rs, rowNum) -> {
                        try {
                            return Post.builder()
                                .id(rs.getLong("id"))
                                .title(rs.getString("name"))
                                .text(rs.getString("post_text"))
                                .tags(rs.getString("tags"))
                                .likesCount(rs.getInt("likes_count"))
                                .image(ImageIO.read(rs.getBlob("picture").getBinaryStream()))
                                .build();
                        } catch (IOException e) {
                            return Post.builder()
                                    .id(rs.getLong("id"))
                                    .title(rs.getString("name"))
                                    .text(rs.getString("post_text"))
                                    .tags(rs.getString("tags"))
                                    .likesCount(rs.getInt("likes_count"))
                                    .build();
                        }
                    }, id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void save(Post post) {
        jdbcTemplate.update("insert into post(name, post_text, tags, likes_count, picture) values(?, ?, ?, ?, ?)",
                post.getTitle(), post.getText(), post.getTags(), post.getLikesCount(), post.getImage());
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("delete from post where id = ?", id);
    }

    @Override
    public void editById(Long id, Post post) {
        jdbcTemplate.update("update post set name=?, post_text=?, tags=?, likes_count=?, picture=? where id = ?",
                post.getTitle(), post.getText(), post.getTags(), post.getLikesCount(), post.getImage(), id);
    }
}
