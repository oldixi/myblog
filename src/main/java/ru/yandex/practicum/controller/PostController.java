package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.model.dto.PostFullDto;
import ru.yandex.practicum.model.dto.PostDto;
import ru.yandex.practicum.model.dto.PostsWithParamsDto;
import ru.yandex.practicum.service.CommentService;
import ru.yandex.practicum.service.PostService;

@Controller
@RequestMapping("/blog/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/")
    public String redirectPosts() {
        return "redirect:/blog/posts";
    }

    /*
    GET "posts" - список постов на странице ленты постов
	Параметры: search - строка с поиском по тегу поста (по умолчанию, пустая строка - все посты)
               pageSize - максимальное число постов на странице (по умолчанию, 10)
               pageNumber - номер текущей страницы (по умолчанию, 1)
    Возвращает: шаблон "posts.html"
    используется модель для заполнения шаблона:
               "posts" - List<Post> - список постов (id, title, text, imagePath, likesCount, comments)
               "search" - строка поиска (по умолчанию, пустая строка - все посты)
               "paging":
               "pageNumber" - номер текущей страницы (по умолчанию, 1)
               "pageSize" - максимальное число постов на странице (по умолчанию, 10)
               "hasNext" - можно ли пролистнуть вперед
               "hasPrevious" - можно ли пролистнуть назад
     */
    @GetMapping
    public String getPosts(Model model,
                           @RequestParam(defaultValue = "", name = "search") String search,
                           @RequestParam(defaultValue = "1", name = "pageNumber") int pageNumber,
                           @RequestParam(defaultValue = "10", name = "pageSize") int pageSize) {
        PostsWithParamsDto posts = postService.getPosts(search, pageNumber, pageSize);
        model.addAttribute("posts", posts.getPosts());
        model.addAttribute("search", search);
        model.addAttribute("paging", posts.getPaging());
        return "posts";
    }

    /*
    GET "/posts/{id}" - страница с постом
    Возвращает: шаблон "post.html"
    используется модель для заполнения шаблона: "post" - модель поста (id, title, text, imagePath, likesCount, comments)
    */
    @GetMapping("/{id}")
    public String getPost(@PathVariable("id") Long id, Model model) {
        PostFullDto post = postService.getPostFullDtoById(id);
        model.addAttribute("post", post);
        return "post";
    }

    /*
    GET "/image/{id}" -эндпоинт, возвращающий набор байт картинки поста
    Параметры: "id" - идентификатор поста
    */
    @GetMapping("/image/{id}")
    @ResponseBody
    public byte[] getImage(@PathVariable("id") Long id) {
        return postService.getImage(id);
    }

    /*
    GET "/posts/add" - страница добавления поста
    Возвращает: шаблон "add-post.html"
    */
    @GetMapping(value = "/add")
    public String addPostPage() {
        return "add-post";
    }

    /*
    POST "/posts" - добавление поста
    Принимает: "multipart/form-data"
    Параметры: "title" - название поста
       		   "text" - текст поста
       		   "image" - файл картинки поста (класс MultipartFile)
       		   "tags" - список тегов поста (по умолчанию, пустая строка)
    Возвращает: редирект на созданный "/posts/{id}"
     */
    @PostMapping()
    public String addPost(@ModelAttribute("post") PostDto post) {
        log.info("Start addPost: postDto={}", post);
        PostFullDto postDto = postService.savePost(post);
        return "redirect:/blog/posts/" + postDto.getId();
    }

    /*
    POST "/posts/{id}/delete" - эндпоинт удаления поста
    Параметры: "id" - идентификатор поста
    Возвращает: редирект на "/posts"
     */
    @PostMapping(value = "/{id}/delete")
    public String deletePost(@PathVariable("id") Long id) {
        postService.deletePostById(id);
        return "redirect:/blog/posts";
    }

    /*
    и) POST "/posts/{id}" - редактирование поста
    Принимает:
            "multipart/form-data"
    Параметры:
            "id" - идентификатор поста
       		"title" - название поста
       		"text" - текст поста
       		"image" - файл картинки поста (класс MultipartFile, может быть null - значит, остается прежним)
       		"tags" - список тегов поста (по умолчанию, пустая строка)
    Возвращает:
    редирект на отредактированный "/posts/{id}"
     */
    @PostMapping("/{id}")
    public String editPost(@PathVariable("id") Long id, @ModelAttribute("post") PostDto post) {
        postService.editPostById(id, post);
        return "redirect:/blog/posts/" + id;
    }

    /*
    POST "/posts/{id}/edit" - страница редактирования поста
    Параметры: "id" - идентификатор поста
    Возвращает: редирект на форму редактирования поста "add-post.html"
    используется модель для заполнения шаблона: "post" - модель поста (id, title, text, imagePath, likesCount, comments)
     */
    @GetMapping(value = "/{id}/edit")
    public String editPostPage(@PathVariable("id") Long id, Model model) {
        PostDto post = postService.getPostDtoById(id);
        model.addAttribute("post", post);
        return "add-post";
    }

    /*
    POST "/posts/{id}/like" - увеличение/уменьшение числа лайков поста
    Параметры: "id" - идентификатор поста, "like" - если true, то +1 лайк, если "false", то -1 лайк
    Возвращает: редирект на "/posts/{id}"
     */
    @PostMapping(value = "/{id}/like")
    public String likePost(@PathVariable("id") Long id, @RequestParam("like") boolean like) {
        postService.likePostById(id, like);
        return "redirect:/blog/posts/" + id;
    }

    /*
    POST "/posts/{id}/comments" - эндпоинт добавления комментария к посту
    Параметры: "id" - идентификатор поста, "text" - текст комментария
    Возвращает: редирект на "/posts/{id}"
    */
    @PostMapping("/{id}/comments")
    public String addComment(Model model,
                             @PathVariable("id") Long id,
                             @RequestParam(defaultValue = "", name = "text") String text) {
        model.addAttribute("text", text);
        commentService.save(id, text);
        return "redirect:/blog/posts/" + id;
    }

    /*
    POST "/posts/{id}/comments/{commentId}/delete" - эндпоинт удаления комментария
    Параметры: "id" - идентификатор поста, "commentId" - идентификатор комментария
    Возвращает: редирект на "/posts/{id}"
    */
    @PostMapping(value = "/{id}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable("id") Long id, @PathVariable("commentId") Long commentId) {
        commentService.deleteById(commentId);
        return "redirect:/blog/posts/" + id;
    }

    /*
    POST "/posts/{id}/comments/{commentId}" - эндпоинт редактирования комментария
    Параметры: "id" - идентификатор поста, "commentId" - идентификатор комментария, "text" - текст комментария
    Возвращает: редирект на "/posts/{id}"
    */
    @PostMapping(value = "/{id}/comments/{commentId}")
    public String editComment(Model model,
                              @PathVariable("id") Long id,
                              @PathVariable("commentId") Long commentId,
                              @RequestParam(defaultValue = "", name = "text") String text) {
        model.addAttribute("text", text);
        commentService.edit(id, commentId, text);
        return "redirect:/blog/posts/" + id;
    }
}
