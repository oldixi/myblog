package ru.yandex.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Comment;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcNativeCommentRepository implements CommentRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Comment> getPostComments(Long postId) {
        return jdbcTemplate.query(
                "select id, comment_text, post_id from comment where post_id = ?",
                (rs, rowNum) -> {
                    return Comment.builder()
                            .id(rs.getLong("id"))
                            .commentText(rs.getString("commentText"))
                            .postId(rs.getLong("postId"))
                            .build();
                }, postId);
    }

    @Override
    public void save(Comment comment) {
        jdbcTemplate.update("insert into comment(comment_text, post_id) values(?, ?)",
                comment.getCommentText(), comment.getPostId());
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("delete from comment where id = ?", id);
    }

    @Override
    public void editById(Long id, Comment comment) {
        jdbcTemplate.update("update comment set comment_text = ?, post_id = ? where id = ?",
                comment.getCommentText(), comment.getPostId(), id);
    }
}
