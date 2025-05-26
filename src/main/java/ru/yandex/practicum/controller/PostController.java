package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.model.PostsWithParams;
import ru.yandex.practicum.service.PostService;

import java.awt.image.BufferedImage;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

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
                           @RequestParam(defaultValue = "") String search,
                           @RequestParam(defaultValue = "1") int pageNumber,
                           @RequestParam(defaultValue = "10") int pageSize) {
        PostsWithParams posts = postService.getPosts(search, pageNumber, pageSize);
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
    public String getPost(Long id, Model model) {
        PostDto post = postService.getPostDtoById(id);
        model.addAttribute("post", post);
        return "post";
    }

    /*
    GET "/images/{id}" -эндпоинт, возвращающий набор байт картинки поста
    Параметры: "id" - идентификатор поста
    */
    @GetMapping("/images/{id}")
    public byte[] getImage(Long id) {
        return postService.getPostById(id).getImage();
    }

    /*
    GET "/posts/add" - страница добавления поста
    Возвращает: шаблон "add-post.html"
    */
    @GetMapping(value = "/add")
    public String addPostPage() {
        return "add-post";
    }

    @PostMapping(value = "/add")
    public String addPost(@ModelAttribute PostDto post) {
        postService.savePost(post);
        return "add-posts";
    }

    /*
    POST "/posts/{id}/delete" - эндпоинт удаления поста
    Параметры: "id" - идентификатор поста
    Возвращает: редирект на "/posts"
     */
    @PostMapping(value = "/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.deletePostById(id);
        return "redirect:/posts";
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

    /*
    POST "/posts/{id}/edit" - страница редактирования поста
    Параметры: "id" - идентификатор поста
    Возвращает: редирект на форму редактирования поста "add-post.html"
    используется модель для заполнения шаблона: "post" - модель поста (id, title, text, imagePath, likesCount, comments)
     */
    @PostMapping(value = "/{id}/edit")
    public String editPost(@PathVariable Long id, @ModelAttribute PostDto post) {
        postService.editPostById(id, post);
        return "redirect:/add-post";
    }

    /*
    POST "/posts/{id}/like" - увеличение/уменьшение числа лайков поста
    Параметры: "id" - идентификатор поста, "like" - если true, то +1 лайк, если "false", то -1 лайк
    Возвращает: редирект на "/posts/{id}"
     */
    @PostMapping(value = "/{id}/like")
    public String likePost(@PathVariable Long id, @RequestParam boolean like) {
        postService.likePostById(id, like);
        return "redirect:/posts/" + id;
    }
}
