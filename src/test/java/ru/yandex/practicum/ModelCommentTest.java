package ru.yandex.practicum;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.mapper.CommentMapper;
import ru.yandex.practicum.model.dto.CommentDto;
import ru.yandex.practicum.model.entity.Comment;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.service.CommentService;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class ModelCommentTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    public void setup() {
    }

    @Test
    @SneakyThrows
    void testAddComments() {
        Post post = Post.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .text("\tЭто тестовый пост новый с картинкой")
                .likesCount(3)
                .tags("test")
                .build();
        Comment comment = Comment.builder()
                .post(post)
                .text("Отличный пост!")
                .build();
        when(commentMapper.toComment(any(CommentDto.class))).thenReturn(comment);
        commentService.save(1L, "Отличный пост!");
        verify(commentRepository).save(comment);
    }

    @Test
    void testEditComments() {
        Post post = Post.builder()
                .id(2L)
                .title("Тестовый пост новый")
                .text("Текст")
                .build();
        Comment comment = Comment.builder()
                .id(2L)
                .post(post)
                .text("Измененный комментарий!")
                .build();
        when(commentMapper.toComment(any(CommentDto.class))).thenReturn(comment);
        commentService.edit(2L, 2L, "Измененный комментарий!");
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void testDeleteComments() {
        commentService.deleteById(1L);
        verify(commentRepository).deleteById(1L);
    }
}
