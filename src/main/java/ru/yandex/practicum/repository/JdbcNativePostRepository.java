package ru.yandex.practicum.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.entity.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcNativePostRepository implements PostRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Post> getPosts(String search, int limit, int offset) {
        log.info("Start getPosts: limit={}, search={}, offset={}", limit, search, offset);
        List<Post> posts = jdbcTemplate.query(
                "select id, name, post_text, tags, likes_count, picture from post where tags like '%'||coalesce(?,'')||'%' order by id desc limit ? offset ?",
                (rs, rowNum) -> {
                    Post post = Post.builder()
                            .id(rs.getLong("id"))
                            .title(rs.getString("name"))
                            .text(rs.getString("post_text"))
                            .tags(rs.getString("tags"))
                            .likesCount(rs.getInt("likes_count"))
                            .build();
                    try {
                        if (rs.getBytes("picture") != null) {
                            System.out.println("Устанавливаем картинку");
                            post.setImage(rs.getBytes("picture"));
                            System.out.println("Устанавили картинку");
                        }
                    } catch (IllegalArgumentException ignored) {
                        log.error("Не смогли прочитать картинку у поста id={}", post.getId());
                    }
                    return post;
                }, search, limit, offset);
        log.info("Отобрано {} постов", posts.size());
        return posts;
    }

    @Override
    public Optional<Post> getById(Long id) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(
                    "select id, name, post_text, tags, likes_count, picture from post where id = ?",
                    (rs, rowNum) -> {
                        Post post = Post.builder()
                                .id(rs.getLong("id"))
                                .title(rs.getString("name"))
                                .text(rs.getString("post_text"))
                                .tags(rs.getString("tags"))
                                .likesCount(rs.getInt("likes_count"))
                                .build();
                        try {
                            if (rs.getBytes("picture") != null) {
                                post.setImage(rs.getBytes("picture"));
                            }
                        } catch (IllegalArgumentException ignored) {
                            log.error("Не смогли прочитать картинку у поста id={}",post.getId());
                        }
                        return post;
                    }, id));
        } catch (Exception e) {
            log.error("Ошибка при получении информации о посте: error={}, msg={}",e.getClass(), e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Long save(Post post) {
        String sql = post.getImage() == null ?
                "insert into post(name, post_text, tags) values(?, ?, ?) returning id" :
                "insert into post(name, post_text, tags, picture) values(?, ?, ?, ?) returning id";
        List<Object> params = formParams(post);
        return Optional.of(jdbcTemplate.queryForObject(sql, Long.class, params.toArray())).orElse(0L);
    }

    @Override
    public void editById(Long id, Post post) {
        String sql = post.getImage() == null ?
                "update post set name = ?, post_text = ?, tags = ? where id = ?" :
                "update post set name = ?, post_text = ?, tags = ?, picture = ? where id = ?";
        List<Object> params = formParams(post);
        params.add(id);
        jdbcTemplate.update(sql, params.toArray());
    }

    @Override
    public void likeById(Long id, int likeCount) {
        jdbcTemplate.update("update post set likes_count = ? where id = ?" , likeCount, id);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("delete from post where id = ?", id);
    }

    @Override
    public int getPostsCount() {
        return jdbcTemplate.queryForObject("select count(1) from post", Integer.class);
    }

    private List<Object> formParams(Post post) {
        List<Object> params = new ArrayList<>(){{
            add(post.getTitle()); add(post.getText()); add(post.getTags());
        }};
        if (post.getImage() != null) params.add(post.getImage());
        return params;
    }
}
