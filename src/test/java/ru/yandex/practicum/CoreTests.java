package ru.yandex.practicum;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.model.dto.CommentDto;
import ru.yandex.practicum.model.dto.PostDto;
import ru.yandex.practicum.model.entity.Post;

import java.util.Optional;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CoreTests {
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    protected Long maxPostId = 0L;

    @BeforeAll
    public void createData() {
        jdbcTemplate.update("insert into posts(title, text, tags) values('Специальный пост', 'Специальный пост', 'comment')");
        jdbcTemplate.update("insert into posts(title, text, tags) values('Специальный пост1', 'Специальный пост1', 'special')");
        maxPostId = jdbcTemplate.queryForObject("select coalesce(max(id),0) from posts", Long.class);
        jdbcTemplate.update("insert into comments(post_id, text) values(?, 'Специальный комментарий')", maxPostId);
    }

    @AfterAll
    void tearDownData() {
        jdbcTemplate.update("delete from posts");
        jdbcTemplate.update("delete from comments");
    }

    protected Optional<PostDto> getLastPost() {
        String sql = "with last_post as (select last_value(p.id) over () max_id,\n" +
                "                          p.*\n" +
                "                   from posts p)\n" +
                "select last_post.id, last_post.title, last_post.text, last_post.tags, last_post.likes_count, last_post.image\n" +
                "from last_post\n" +
                "where last_post.id = last_post.max_id";

        return Optional.of(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> PostDto.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .text(rs.getString("text"))
                .tags(rs.getString("tags"))
                .image(new MockMultipartFile("postImage" + rs.getLong("id"), rs.getBytes("image")))
                .build()));
    }

    protected Optional<Post> getAnyPost() {
        String sql = "select id, title, text, tags, likes_count, image\n" +
                "from posts\n" +
                "limit 1";

        return Optional.of(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> Post.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .text(rs.getString("text"))
                .tags(rs.getString("tags"))
                .likesCount(rs.getInt("likes_count"))
                .image(rs.getBytes("image"))
                .build()));
    }

    protected Optional<CommentDto> getLastComment() {
        String sql = "with last_comment as (select last_value(c.id) over () max_id,\n" +
                "                          c.*\n" +
                "                   from comments c)\n" +
                "select last_comment.id, last_comment.post_id, last_comment.text\n" +
                "from last_comment\n" +
                "where last_comment.id = last_comment.max_id";

        return Optional.of(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> CommentDto.builder()
                .id(rs.getLong("id"))
                .text(rs.getString("text"))
                .postId(rs.getLong("post_id"))
                .build()));
    }
}
