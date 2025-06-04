package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.controller.PostController;
import ru.yandex.practicum.model.dto.CommentDto;
import ru.yandex.practicum.model.dto.PostDto;
import ru.yandex.practicum.model.entity.Post;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class IntegrationControllerTest extends CoreTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostController postController;
    @Test
    void testContoller() {
        assertNotNull(postController);
    }

    @Test
    void testGetPosts() throws Exception {
        mockMvc.perform(get("/blog/posts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"));
    }

    @Test
    void testGetPageablePosts() throws Exception {
        mockMvc.perform(get("/blog/posts")
                        .param("search", "")
                        .param("pageNumber", "1")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"));
    }

    @Test
    void testGetPost() throws Exception {
        Post post = getAnyPost().orElse(new Post());
        mockMvc.perform(get("/blog/posts/" + post.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    void testDeletePost() throws Exception {
        Post post = getAnyPost().orElse(new Post());
        mockMvc.perform(post("/blog/posts/" + post.getId() + "/delete" ))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog/posts"));
    }

    @Test
    void testEditPostsPage() throws Exception {
        Post post = getAnyPost().orElse(new Post());
        mockMvc.perform(get("/blog/posts/" + post.getId() + "/edit"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("add-post"))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    void testEditPost() throws Exception {
        PostDto postFromDb = getLastPost().orElse(new PostDto());
        postFromDb.setTitle("New title");
        mockMvc.perform(post("/blog/posts/" + postFromDb.getId())
                        .contentType("application/x-www-form-urlencoded")
                        .flashAttr("post", postFromDb))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog/posts/" + postFromDb.getId()));
    }

    @Test
    void testAddPostPage() throws Exception {
        mockMvc.perform(get("/blog/posts/add"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("add-post"));
    }

    @Test
    void testAddPost() throws Exception {
        PostDto postFromDb = getLastPost().orElse(new PostDto());
        Long lastId = postFromDb.getId();
        postFromDb.setId(null);
        postFromDb.setTitle("Пост для проверки добавления");
        mockMvc.perform(post("/blog/posts")
                        .contentType("application/x-www-form-urlencoded")
                        .flashAttr("post", postFromDb))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog/posts/" + (lastId + 1)));
    }

    @Test
    void testLikePost() throws Exception {
        Post postFromDb = getAnyPost().orElse(new Post());
        mockMvc.perform(post("/blog/posts/" + postFromDb.getId() + "/like")
                        .param("like", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog/posts/" + postFromDb.getId()));
    }

    @Test
    void testAddComment() throws Exception {
        Post postFromDb = getAnyPost().orElse(new Post());
        mockMvc.perform(post("/blog/posts/" + postFromDb.getId() + "/comments")
                        .queryParam("text", "комментарий"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog/posts/" + postFromDb.getId()));
    }

    @Test
    void testEditComment() throws Exception {
        Post postFromDb = getAnyPost().orElse(new Post());
        CommentDto commentFromDb = getLastComment().orElse(new CommentDto());
        mockMvc.perform(post("/blog/posts/" + postFromDb.getId() + "/comments/" + commentFromDb.getId())
                        .queryParam("text", "комментарий"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog/posts/" + postFromDb.getId()));
    }

    @Test
    void testDeleteComment() throws Exception {
        Post postFromDb = getAnyPost().orElse(new Post());
        CommentDto commentFromDb = getLastComment().orElse(new CommentDto());
        mockMvc.perform(post("/blog/posts/" + postFromDb.getId() + "/comments/" + commentFromDb.getId() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog/posts/" + postFromDb.getId()));
    }
}
