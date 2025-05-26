import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import ru.yandex.practicum.configuration.WebConfiguration;
import ru.yandex.practicum.controller.CommentController;
import ru.yandex.practicum.controller.HomeController;
import ru.yandex.practicum.controller.PostController;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.mapper.PostMapper;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.service.CommentService;
import ru.yandex.practicum.service.PostService;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {WebConfiguration.class})
@WebAppConfiguration
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CoreTests {
    @Autowired
    private HomeController homeController;
    @Autowired
    private PostController postController;
    @Autowired
    private CommentController commentController;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostMapper postMapper;
    private Long maxPostId = 0L;
    private Long maxCommentId = 0L;

    @BeforeAll
    public void createData() {
        maxPostId = jdbcTemplate.queryForObject("select coalesce(max(id),0) from post", Long.class);
        jdbcTemplate.update("insert into post(name, post_text, tags) values('Специальный пост', 'Специальный пост', 'comment')");
        System.out.println("Перед тестами максимальный id постов = " + maxPostId);
        maxCommentId = jdbcTemplate.queryForObject("select coalesce(max(id),0) from comment", Long.class);
        maxPostId = jdbcTemplate.queryForObject("select coalesce(max(id),0) from post", Long.class);
        jdbcTemplate.update("insert into comment(post_id, comment_text) values(?, 'Специальный комментарий')", maxPostId);
        System.out.println("Перед тестами максимальный id комментариев = " + maxCommentId);
    }

    @AfterAll
    void deleteData() {
        System.out.println("После тестов удаляем все посты, начиная с id = " + maxPostId);
        jdbcTemplate.update("delete from post where id >= ?", maxPostId);
        System.out.println("После тестов удаляем все комментарии, начиная с id = " + maxPostId);
        jdbcTemplate.update("delete from comment where id >= ?", maxCommentId);
    }

    @Test
    void testHome() {
        assertNotNull(homeController);
        assertNotNull(postController);
        assertNotNull(commentController);
    }

    @Test
    void testGetPost() {
        jdbcTemplate.update("insert into post(name, post_text, tags) values('Предзагруженный пост', 'Предзагруженный пост', 'тестовый')");
        Post insertedPost = getLastPost().orElse(null);
        assertNotNull(insertedPost);
        assertNotNull(insertedPost.getId());

        Post post = postService.getPostById(insertedPost.getId());
        assertNotNull(post);
        assertNotNull(post.getId());
        assertEquals("Предзагруженный пост", post.getTitle());
        assertEquals("Предзагруженный пост", post.getText());
        assertEquals("тестовый", post.getTags());
        assertEquals(0, post.getLikesCount());
    }

    @Test
    void testGetPostDto() {
        PostDto postDto = PostDto.builder()
                .title("Тестовый пост новый")
                .text("\tЭто тестовый пост новый с картинкой")
                .likesCount(5)
                .imagePath("myblogdb.png")
                .comments(new ArrayList<>())
                .build();
        postService.savePost(postDto);
        Post insertedPost = getLastPost().orElse(null);
        assertNotNull(insertedPost);
        assertNotNull(insertedPost.getId());

        PostDto post = postService.getPostDtoById(insertedPost.getId());
        assertNotNull(post);
        assertNotNull(post.getId());
        assertEquals("Тестовый пост новый", post.getTitle());
        assertEquals("\tЭто тестовый пост новый с картинкой", post.getText());
        assertEquals(5, post.getLikesCount());
        assertNotNull(insertedPost.getImage());
    }

    @Test
    void testAddPost() {
        Post post = Post.builder()
                .title("Тестовый пост")
                .text("\tЭто тестовый пост для сохранения")
                .build();
        postService.savePost(postMapper.toDto(post));
        Post insertedPost = getLastPost().orElse(null);
        assertNotNull(insertedPost);
        assertNotNull(insertedPost.getId());
        assertEquals("Тестовый пост", insertedPost.getTitle());
        assertEquals("\tЭто тестовый пост для сохранения", insertedPost.getText());
        assertEquals(0, insertedPost.getLikesCount());
    }

    @Test
    void testEditPost() {
        Post post1 = Post.builder()
                .id(1L)
                .title("Тестовый пост для изменения полей")
                .text("\tЭто тестовый пост 1\n\tЕще один абзац")
                .tags("test")
                .build();
        postService.editPostById(1L, postMapper.toDto(post1));
        Post editedPost = postService.getPostById(1L);
        assertNotNull(editedPost);
        assertNotNull(editedPost.getId());
        assertEquals("Тестовый пост для изменения полей", editedPost.getTitle());
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
        Post post2 = Post.builder()
                .title("Тестовый пост новый")
                .text("\tЭто тестовый пост новый с картинкой")
                .likesCount(5)
                .build();
        /*try {
            post2.setImage(ImageIO.read(new File("myblogdb.png")));
        } catch (IOException e) {
            System.out.println("Картинка не найдена");
        }*/
        try {
            post2.setImage(Files.readAllBytes(new File("myblogdb.png").toPath()));
        } catch (IOException e) {
            System.out.println("Картинка не найдена");
        }
        PostDto postDto = postMapper.toDto(post2);
        System.out.println("Image path :" + postDto.getImagePath());
        postService.savePost(postDto);

        Post insertedPost = getLastPost().orElse(null);
        assertNotNull(insertedPost);
        assertNotNull(insertedPost.getId());
        assertNotNull(insertedPost.getImage());

        Post post = postService.getPostById(insertedPost.getId());
        assertNotNull(post);
        assertNotNull(post.getId());
        assertNotNull(post.getImage());
        assertEquals(insertedPost.getImage(), post.getImage());
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
                .commentText("Отличный пост!")
                .build();
        Comment comment2 = Comment.builder()
                .id(insertedComments.getId() + 2)
                .postId(insertedPost.getId())
                .commentText("Я тоже так считаю. Поставлю лайк")
                .build();

        commentService.save(insertedPost.getId(), comment1.getCommentText());
        commentService.save(insertedPost.getId(), comment2.getCommentText());

        Object[] comments = commentService.getPostComments(insertedPost.getId()).toArray();
        assertEquals(2, comments.length);
        assertArrayEquals(new ArrayList<Comment>() {{add(comment1); add(comment2);}}.toArray(), comments);
    }

    @Test
    void testDeleteComments() {
        Post insertedPost = getLastPost().orElse(null);
        assertNotNull(insertedPost);
        assertNotNull(insertedPost.getId());

        Comment comment1 = Comment.builder()
                .postId(insertedPost.getId())
                .commentText("Отличный пост!")
                .build();
        Comment comment2 = Comment.builder()
                .postId(insertedPost.getId())
                .commentText("Я тоже так считаю. Поставлю лайк")
                .build();

        commentService.save(insertedPost.getId(), comment1.getCommentText());
        commentService.save(insertedPost.getId(), comment2.getCommentText());

        Comment insertedComments = getLastComment().orElse(null);
        assertNotNull(insertedComments);
        assertNotNull(insertedComments.getId());

        commentService.deleteById(insertedComments.getId());
        assertEquals(1, commentService.getPostComments(insertedPost.getId()).toArray().length);
/*
        commentService.deleteById(insertedComments.getId() - 1);
        assertEquals(0, commentService.getPostComments(insertedPost.getId()).toArray().length);*/
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
                .commentText(rs.getString("comment_text"))
                .postId(rs.getLong("post_id"))
                .build()));
    }
}
