package ru.yandex.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Post;

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
                    Post post = Post.builder()
                            .id(rs.getLong("id"))
                            .title(rs.getString("name"))
                            .text(rs.getString("post_text"))
                            .tags(rs.getString("tags"))
                            .likesCount(rs.getInt("likes_count"))
                            .build();
                    try {
                        /*Blob imageFromDb = rs.getBlob("picture");
                        if (imageFromDb != null) {
                            post.setImage(ImageIO.read(imageFromDb.getBinaryStream()));
                        }*/
                        if (rs.getBytes("picture") != null) {
                            System.out.println("Устанавливаем картинку");
                            //post.setImage(ImageIO.read(rs.getBytes("picture")));
                            post.setImage(rs.getBytes("picture"));
                            System.out.println("Устанавили картинку");
                        }
                    } catch (IllegalArgumentException ignored) {
                        System.out.println("Не смогли прочитать картинку у поста id=" + post.getId());
                    }
                    return post;
                }, search, limit);
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
                        System.out.println("Смотрим пост id=" + post.getId());
                        try {
                            /*if (!Objects.equals(rs.getBinaryStream("picture"), InputStream.nullInputStream())) {
                                System.out.println("Устанавливаем картинку");
                                post.setImage(ImageIO.read(rs.getBinaryStream("picture")));
                                System.out.println("Устанавили картинку");
                            }*/
                            if (rs.getBytes("picture") != null) {
                                System.out.println("Устанавливаем картинку");
                                post.setImage(rs.getBytes("picture"));
                                System.out.println("Устанавили картинку");
                            }
                        } catch (IllegalArgumentException ignored) {
                            System.out.println("Не смогли прочитать картинку у поста id=" + post.getId());
                        }
                        System.out.println("Возвращаем пост id=" + post.getId() + ", title=" + post.getTitle() +
                                ", is image null? - " + (post.getImage() == null));
                        System.out.println("RETURN");
                        return post;
                    }, id));
        } catch (Exception e) {
            System.out.println("Грохнулись error=" + e.getClass() + ", msg=" + e.getMessage() );
            return Optional.empty();
        }
    }

    @Override
    public void save(Post post) {
        if (post.getImage() != null) {
            //byte[] imageBytes = ((DataBufferByte) post.getImage().getData().getDataBuffer()).getData();
            byte[] imageBytes = post.getImage();
            jdbcTemplate.update("insert into post(name, post_text, tags, likes_count, picture) values(?, ?, ?, ?, ?)",
                    post.getTitle(), post.getText(), post.getTags(), post.getLikesCount(), imageBytes);
        } else
            jdbcTemplate.update("insert into post(name, post_text, tags, likes_count) values(?, ?, ?, ?)",
                    post.getTitle(), post.getText(), post.getTags(), post.getLikesCount());
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("delete from post where id = ?", id);
    }

    @Override
    public void editById(Long id, Post post) {
        jdbcTemplate.update("update post set name = ?, post_text = ?, tags = ?, likes_count = ?, picture = ? where id = ?",
                post.getTitle(), post.getText(), post.getTags(), post.getLikesCount(), post.getImage(), id);
    }
}
