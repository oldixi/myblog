import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.model.dto.PostDto;
import ru.yandex.practicum.model.dto.PostFullDto;
import ru.yandex.practicum.model.dto.PostsWithParamsDto;
import ru.yandex.practicum.model.entity.Comment;
import ru.yandex.practicum.model.entity.Post;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTests extends CoreTests {
    @Test
    void testContoller() {
        assertNotNull(postController);
    }

    @Test
    void testGetPost() {
        Post insertedPost = getLastPost().orElse(null);
        assertNotNull(insertedPost);
        assertNotNull(insertedPost.getId());
        Post post = postService.getPostById(insertedPost.getId());
        assertNotNull(post);
        assertNotNull(post.getId());
        assertNotNull(post.getTitle());
        assertNotNull(post.getText());
    }

    @Test
    void testGetPosts() {
        PostsWithParamsDto posts = postService.getPosts(null, 1, 10);
        assertNotNull(posts);
        assertNotNull(posts.getPosts());
        assertNotNull(posts.getPaging());
        posts.getPosts().forEach(System.out::println);
    }

    @ParameterizedTest
    @ValueSource(strings = {"\tЭто тестовый пост новый с картинкой", "\tЭто тестовый \tпост новый с \tкартинкой"})
    void testGetPostDto(String text) {
        PostDto postDto = PostDto.builder()
                .title("Тестовый пост новый")
                .text(text)
                .build();
        try {
            MultipartFile picture = new MockMultipartFile("myblogdb.png",
                    Files.readAllBytes(new File("myblogdb.png").toPath()));
            postDto.setImage(picture);
        } catch (IOException e) {
            System.out.println("Картинка не найдена");
        }
        PostFullDto post = postService.savePost(postDto);
        assertNotNull(post);
        assertNotNull(post.getId());
        assertEquals("Тестовый пост новый", post.getTitle());
        assertNotNull(post.getTextPreview());
    }

    @Test
    void testAddPost() {
        PostDto post = PostDto.builder()
                .title("Тестовый пост")
                .text("\tЭто тестовый пост для сохранения")
                .build();
        PostFullDto insertedPost = postService.savePost(post);
        assertNotNull(insertedPost);
        assertNotNull(insertedPost.getId());
        assertEquals("Тестовый пост", insertedPost.getTitle());
        assertEquals("\tЭто тестовый пост для сохранения", insertedPost.getTextPreview());
        assertEquals(0, insertedPost.getLikesCount());
    }

    @Test
    void testEditPost() {
        Long id = 0L;
        Post lastPost = getLastPost().orElse(null);
        if (lastPost != null) id = lastPost.getId();;
        PostDto post1 = PostDto.builder()
                .id(id)
                .title("Тестовый пост для изменения полей")
                .text("\tЭто тестовый пост 1\n\tЕще один абзац")
                .tags("test")
                .build();
        postService.editPostById(id, post1);
        Post editedPost = postService.getPostById(id);
        assertNotNull(editedPost);
        assertNotNull(editedPost.getId());
        assertEquals("Тестовый пост для изменения полей", editedPost.getTitle());
        assertEquals("\tЭто тестовый пост 1\n\tЕще один абзац", editedPost.getText());
        assertEquals("test", editedPost.getTags());
    }

    @Test
    void testEditEmptyPicturePost() {
        PostDto post1 = PostDto.builder()
                .title("Тестовый пост для изменения полей")
                .text("\tЭто тестовый пост 1\n\tЕще один абзац")
                .tags("test")
                .build();
        try {
            MultipartFile picture = new MockMultipartFile("myblogdb.png",
                    Files.readAllBytes(new File("myblogdb.png").toPath()));
            post1.setImage(picture);
        } catch (IOException e) {
            System.out.println("Картинка не найдена");
        }
        PostFullDto insertedPost = postService.savePost(post1);
        assertNotNull(insertedPost);
        assertNotNull(insertedPost.getId());

        post1.setImage(null);
        post1.setTitle("New");
        postService.editPostById(insertedPost.getId(), post1);
        Post editedPost = postService.getPostById(insertedPost.getId());
        assertNotNull(editedPost);
        assertNotNull(editedPost.getId());
        assertNotNull(editedPost.getImage());
        assertEquals("New", editedPost.getTitle());
        assertEquals("\tЭто тестовый пост 1\n\tЕще один абзац", editedPost.getText());
        assertEquals("test", editedPost.getTags());
        assertEquals(0, editedPost.getLikesCount());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false, false})
    void testAddLikePost(boolean like) {
        Post insertedPost = getLastPost().orElse(null);
        assertNotNull(insertedPost);
        assertNotNull(insertedPost.getId());

        Post post = postService.getPostById(insertedPost.getId());
        postService.likePostById(insertedPost.getId(), like);
        Post editedPost = postService.getPostById(insertedPost.getId());
        assertNotNull(post);
        assertNotNull(editedPost);
        assertEquals(like ? post.getLikesCount() + 1 : (post.getLikesCount() > 0 ? post.getLikesCount() - 1 : 0),
                editedPost.getLikesCount());
    }

    @Test
    void getImage() {
        PostDto post2 = PostDto.builder()
                .title("Тестовый пост новый")
                .text("\tЭто тестовый пост новый с картинкой")
                .build();
        try {
            MultipartFile picture = new MockMultipartFile("myblogdb.png",
                    Files.readAllBytes(new File("myblogdb.png").toPath()));
            post2.setImage(picture);
        } catch (IOException e) {
            System.out.println("Картинка не найдена");
        }
        PostFullDto insertedPost = postService.savePost(post2);
        assertNotNull(insertedPost);
        assertNotNull(insertedPost.getId());
        byte[] image = postService.getImage(insertedPost.getId());
        assertNotNull(image);
    }

    @Test
    void testDeletePost() {
        postService.deletePostById(1L);
        Post post = postService.getPostById(1L);
        assertNotNull(post);
        assertNull(post.getId());
    }

    @Test
    void testAddComments() {
        Post insertedPost = getLastPost().orElse(null);
        assertNotNull(insertedPost);
        assertNotNull(insertedPost.getId());

        Comment insertedComments = getLastComment().orElse(null);
        assertNotNull(insertedComments);
        assertNotNull(insertedComments.getId());

        Comment comment1 = Comment.builder()
                .id(insertedComments.getId() + 1)
                .postId(insertedPost.getId())
                .text("Отличный пост!")
                .build();

        commentService.save(insertedPost.getId(), comment1.getText());

        List<Comment> comments = commentService.getPostComments(insertedPost.getId());
        assertTrue(comments.size() >= 1);
        assertTrue(comments.stream().anyMatch(comm -> "Отличный пост!".equals(comm.getText())));
    }

    @Test
    void testEditComments() {
        Post insertedPost = getLastPost().orElse(null);
        assertNotNull(insertedPost);
        assertNotNull(insertedPost.getId());
        List<Comment> comments = commentService.getPostComments(insertedPost.getId());

        Comment insertedComments = getLastComment().orElse(null);
        assertNotNull(insertedComments);
        assertNotNull(insertedComments.getId());

        commentService.save(insertedPost.getId(), "Отличный пост!!!");
        insertedComments = getLastComment().orElse(null);
        assertNotNull(insertedComments);
        assertNotNull(insertedComments.getId());
        final Long commentId = insertedComments.getId();
        final String text = "Меня комментарий с 'Отличный пост!' на 'Самый лучший пост!'";

        commentService.edit(insertedPost.getId(), commentId, text);

        comments = commentService.getPostComments(insertedPost.getId());
        assertTrue(comments.stream().anyMatch(comm -> commentId.equals(comm.getId()) && text.equals(comm.getText())));
    }

    @Test
    void testDeleteComments() {
        Post insertedPost = getLastPost().orElse(null);
        assertNotNull(insertedPost);
        assertNotNull(insertedPost.getId());

        Comment comment1 = Comment.builder()
                .postId(insertedPost.getId())
                .text("Отличный пост!")
                .build();
        Comment comment2 = Comment.builder()
                .postId(insertedPost.getId())
                .text("Я тоже так считаю. Поставлю лайк")
                .build();

        commentService.save(insertedPost.getId(), comment1.getText());
        commentService.save(insertedPost.getId(), comment2.getText());

        Comment insertedComments = getLastComment().orElse(null);
        assertNotNull(insertedComments);
        assertNotNull(insertedComments.getId());

        PostFullDto insertedPostDto = postService.getPostFullDtoById(insertedPost.getId());
        commentService.deleteById(insertedComments.getId());
        assertEquals(insertedPostDto.getComments().size() - 1, commentService.getPostComments(insertedPost.getId()).toArray().length);
    }

    private Optional<Post> getLastPost() {
        String sql = "with last_post as (select last_value(p.id) over () max_id,\n" +
                "                          p.*\n" +
                "                   from post p)\n" +
                "select last_post.id, last_post.name, last_post.post_text, last_post.tags, last_post.likes_count, last_post.picture\n" +
                "from last_post\n" +
                "where last_post.id = last_post.max_id";

        return Optional.of(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> Post.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("name"))
                .text(rs.getString("post_text"))
                .tags(rs.getString("tags"))
                .likesCount(rs.getInt("likes_count"))
                .image(rs.getBytes("picture"))
                .build()));
    }

    private Optional<Comment> getLastComment() {
        String sql = "with last_comment as (select last_value(c.id) over () max_id,\n" +
                "                          c.*\n" +
                "                   from comment c)\n" +
                "select last_comment.id, last_comment.post_id, last_comment.comment_text\n" +
                "from last_comment\n" +
                "where last_comment.id = last_comment.max_id";

        return Optional.of(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> Comment.builder()
                .id(rs.getLong("id"))
                .text(rs.getString("comment_text"))
                .postId(rs.getLong("post_id"))
                .build()));
    }
}
