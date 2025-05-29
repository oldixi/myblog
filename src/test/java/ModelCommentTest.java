import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.yandex.practicum.model.entity.Comment;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.service.CommentService;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@Slf4j
public class ModelCommentTest extends CoreTests {
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeAll
    public static void setUpBeforeClass() {
        log.info("Запускаем тесты");
    }

    @AfterAll
    public static void tearDownAfterClass() {
        log.info("Все тесты проведены");
    }

    @BeforeEach
    public void setup() {
    }

    @Test
    @SneakyThrows
    void testAddComments() {
        Comment comment1 = Comment.builder()
                .postId(1L)
                .text("Отличный пост!")
                .build();
        commentService.save(1L, "Отличный пост!");
        verify(commentRepository).save(comment1);
    }

    @Test
    void testEditComments() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .postId(1L)
                .text("Измененный комментарий!")
                .build();
        commentService.edit(1L, 1L, "Измененный комментарий!");
        verify(commentRepository, times(1)).editById(1L, comment1);
    }

    @Test
    void testDeleteComments() {
        commentService.deleteById(1L);
        verify(commentRepository).deleteById(1L);
    }
}
