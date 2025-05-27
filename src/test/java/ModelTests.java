import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.repository.PostRepository;
import ru.yandex.practicum.service.CommentService;
import ru.yandex.practicum.service.PostService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class ModelTests extends CoreTests {
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private PostService postService;
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

/*    @Test
    public void testOrderCreation() {
        // Настраиваем mock для успешной обработки платежа
        when(paymentService.processPayment(anyDouble())).thenReturn(true);

        // Проверяем создание заказа
        boolean result = postService1.createOrder(100.0);
        assertTrue(result);

        // Проверяем, что метод processPayment вызывался один раз
        verify(paymentService, times(1)).processPayment(100.0);
    }

    @Test
    @Disabled("Тест пропущен, требуется исправление логики отмены заказа")
    public void testOrderCancellation() {
        // Тест игнорируется, так как логика отмены требует исправления
        boolean result = postService1.cancelOrder(123);
        assertTrue(result);
    }

    @Test
    @Tag("processing")
    public void testOrderProcessing() {
        // Проверяем, что заказ может быть обработан
        boolean result = postService1.processOrder(123);
        assertTrue(result);
    }*/
}
