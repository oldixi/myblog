package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.CommentService;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
@ResponseBody
public class CommentController {
    private final CommentService commentService;

    /*
    POST "/posts/{id}/comments" - эндпоинт добавления комментария к посту
    Параметры: "id" - идентификатор поста, "text" - текст комментария
    Возвращает: редирект на "/posts/{id}"
    */
    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable("id") Long id, @RequestBody String text) {
        commentService.save(id, text);
        return "redirect:/posts/"+id;
    }

    /*
    POST "/posts/{id}/comments/{commentId}/delete" - эндпоинт удаления комментария
    Параметры: "id" - идентификатор поста, "commentId" - идентификатор комментария
    Возвращает: редирект на "/posts/{id}"
    */
    @PostMapping(value = "/{id}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable("id") Long id, @PathVariable("commentId") Long commentId) {
        commentService.deleteById(commentId);
        return "redirect:/posts/"+id;
    }

    /*
    POST "/posts/{id}/comments/{commentId}" - эндпоинт редактирования комментария
    Параметры: "id" - идентификатор поста, "commentId" - идентификатор комментария, "text" - текст комментария
    Возвращает: редирект на "/posts/{id}"
    */
    @PostMapping(value = "/{id}/comments/{commentId}")
    public String editComment(@PathVariable("id") Long id, @PathVariable("commentId") Long commentId, @RequestBody String text) {
        commentService.edit(id, commentId, text);
        return "redirect:/posts/"+id;
    }
}
