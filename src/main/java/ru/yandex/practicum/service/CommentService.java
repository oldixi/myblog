package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.entity.Comment;
import ru.yandex.practicum.repository.CommentRepository;

import java.util.List;

@Service
//При таком внедрении не работают Mock-тесты -> пришлось отказаться в пользу не самого хорошего способа внедрения
//через @Autowired
//@RequiredArgsConstructor
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getPostComments(Long postId) {
        return commentRepository.getPostComments(postId);
    }

    @Transactional
    public void save(Long postId, String text) {
        Comment comment = Comment.builder()
                .text(text)
                .postId(postId)
                .build();
        commentRepository.save(comment);
    }

    @Transactional
    public void edit(Long postId, Long id, String text) {
        Comment comment = Comment.builder()
                .postId(postId)
                .text(text)
                .id(id)
                .build();
        commentRepository.editById(id, comment);
    }

    @Transactional
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }
}
