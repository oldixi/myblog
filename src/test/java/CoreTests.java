import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import ru.yandex.practicum.configuration.WebConfiguration;
import ru.yandex.practicum.controller.PostController;
import ru.yandex.practicum.mapper.PostMapper;
import ru.yandex.practicum.service.CommentService;
import ru.yandex.practicum.service.PostService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {WebConfiguration.class})
@WebAppConfiguration
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CoreTests {
    @Autowired
    protected PostController postController;
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    protected PostService postService;
    @Autowired
    protected CommentService commentService;
    @Autowired
    protected PostMapper postMapper;
    protected Long maxPostId = 0L;
    protected Long maxCommentId = 0L;

    @BeforeAll
    public void createData() {
        maxPostId = jdbcTemplate.queryForObject("select coalesce(max(id),0) from post", Long.class);
        jdbcTemplate.update("insert into post(name, post_text, tags) values('Специальный пост', 'Специальный пост', 'comment')");
        jdbcTemplate.update("insert into post(name, post_text, tags) values('Специальный пост1', 'Специальный пост1', 'special')");
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
}
