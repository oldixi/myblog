import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.mapper.PostMapper;
import ru.yandex.practicum.model.dto.PostDto;
import ru.yandex.practicum.model.dto.PostFullDto;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.service.CommentService;
import ru.yandex.practicum.service.PostService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@Slf4j
public class ModelPostTests extends CoreTests {
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentService commentService;

    @InjectMocks
    private PostService postService;

    @BeforeAll
    public static void setUpBeforeClass() {
        log.info("Запускаем тесты");
    }

    @AfterAll
    public static void tearDownAfterClass() {
        log.info("Все тесты проведены");
    }

    @Test
    void testGetPost() {
        Post post = Post.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .text("\tЭто тестовый пост новый с картинкой")
                .likesCount(3)
                .tags("test")
                .build();
        try {
            MultipartFile picture = new MockMultipartFile("myblogdb.png",
                    Files.readAllBytes(new File("myblogdb.png").toPath()));
            post.setImage(picture.getBytes());
        } catch (IOException e) {
            System.out.println("Картинка не найдена");
        }

        when(postRepository.getById(anyLong())).thenReturn(Optional.of(post));

        Post testPost = postService.getPostById(1L);
        assertNotNull(testPost);
        assertNotNull(testPost.getId());
        assertEquals(post, testPost);
    }

    @ParameterizedTest
    @ValueSource(strings = {"\tЭто тестовый пост новый с картинкой", "\tЭто тестовый пост новый с картинкой\n\tВторой абзац"})
    void testGetPostDto(String text) {
        Post post = Post.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .text(text)
                .build();
        try {
            MultipartFile picture = new MockMultipartFile("myblogdb.png",
                    Files.readAllBytes(new File("myblogdb.png").toPath()));
            post.setImage(picture.getBytes());
        } catch (IOException e) {
            System.out.println("Картинка не найдена");
        }
        PostFullDto postDto = PostFullDto.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .textPreview("\tЭто тестовый пост новый с картинкой")
                .build();

        when(postRepository.getById(anyLong())).thenReturn(Optional.of(post));
        when(postMapper.toDto(any(Post.class))).thenReturn(postDto);
        when(commentService.getPostComments(anyLong())).thenReturn(null);

        PostFullDto testPost = postService.getPostDtoById(1L);
        assertNotNull(testPost);
        assertNotNull(testPost.getId());
        System.out.println(postDto.getTextPreview());
        assertEquals(postDto, testPost);

    }

    @Test
    void testAddPost() {
        Post post = Post.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .text("\tЭто тестовый пост новый с картинкой")
                .likesCount(0)
                .tags("test")
                .build();
        try {
            MultipartFile picture = new MockMultipartFile("myblogdb.png",
                    Files.readAllBytes(new File("myblogdb.png").toPath()));
            post.setImage(picture.getBytes());
        } catch (IOException e) {
            System.out.println("Картинка не найдена");
        }

        PostDto postDto = PostDto.builder()
                .title("Тестовый пост")
                .text("\tЭто тестовый пост для сохранения")
                .tags("test")
                .build();
        try {
            MultipartFile picture = new MockMultipartFile("myblogdb.png",
                    Files.readAllBytes(new File("myblogdb.png").toPath()));
            postDto.setImage(picture);
        } catch (IOException e) {
            System.out.println("Картинка не найдена");
        }

        PostFullDto postFullDto = PostFullDto.builder()
                .id(1L)
                .title("Тестовый пост")
                .textPreview("Это тестовый пост для сохранения")
                .tags("test")
                .build();

        when(postMapper.toPost(any(PostDto.class))).thenReturn(post);
        when(postRepository.getById(anyLong())).thenReturn(Optional.of(post));
        when(commentRepository.getPostComments(anyLong())).thenReturn(new ArrayList<>());
        when(postRepository.save(any(Post.class))).thenReturn(1L);

        PostFullDto insertedPost = postService.savePost(postDto);
        assertNotNull(insertedPost);
        assertNotNull(insertedPost.getId());
    }

    @Test
    void testEditPost() {
        Post post = Post.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .text("\tЭто тестовый пост новый с картинкой")
                .likesCount(0)
                .tags("test")
                .build();

        PostDto postDto = PostDto.builder()
                .id(1L)
                .title("Тестовый пост для изменения полей")
                .text("\tЭто тестовый пост 1\n\tЕще один абзац")
                .tags("test")
                .build();

        when(postMapper.toPost(any(PostDto.class))).thenReturn(post);
        postService.editPostById(1L, postDto);
        verify(postRepository, times(1)).editById(1L, post);
    }

    @Test
    void testAddLikePost() {
        Post post = Post.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .text("\tЭто тестовый пост новый с картинкой")
                .likesCount(3)
                .tags("test")
                .build();

        when(postRepository.getById(anyLong())).thenReturn(Optional.of(post));
        postService.likePostById(1L, true);
        verify(postRepository, times(1)).likeById(1L, 4);
    }

    @Test
    void testDislikePost() {
        Post post = Post.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .text("\tЭто тестовый пост новый с картинкой")
                .likesCount(2)
                .build();

        when(postRepository.getById(anyLong())).thenReturn(Optional.of(post));
        postService.likePostById(1L, false);
        verify(postRepository, times(1)).likeById(1L, 1);
    }

    @Test
    void getImage() {
        Post post = Post.builder()
                .id(1L)
                .title("Тестовый пост новый")
                .text("\tЭто тестовый пост новый с картинкой")
                .build();
        try {
            MultipartFile picture = new MockMultipartFile("myblogdb.png",
                    Files.readAllBytes(new File("myblogdb.png").toPath()));
            post.setImage(picture.getBytes());
        } catch (IOException e) {
            System.out.println("Картинка не найдена");
        }

        when(postRepository.getById(anyLong())).thenReturn(Optional.of(post));

        byte[] image = postService.getImage(1L);
        assertNotNull(image);
    }

    @Test
    void testDeletePost() {
        postService.deletePostById(1L);
        verify(postRepository, times(1)).deleteById(1L);
    }
}
