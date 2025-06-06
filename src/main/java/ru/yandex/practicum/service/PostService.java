package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.dto.PostFullDto;
import ru.yandex.practicum.model.dto.PostDto;
import ru.yandex.practicum.mapper.PostMapper;
import ru.yandex.practicum.model.dto.PagingParametersDto;
import ru.yandex.practicum.model.entity.Post;
import ru.yandex.practicum.model.dto.PostsWithParamsDto;
import ru.yandex.practicum.repository.PostRepository;

import java.util.List;

@Service
//При таком внедрении не работают Mock-тесты -> пришлось отказаться в пользу не самого хорошего способа внедрения
//через @Autowired
//@RequiredArgsConstructor
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostMapper postMapper;

    public PostsWithParamsDto getPosts(String search, int pageNumber, int pageSize) {
        List<Post> posts = postRepository.getPosts(search, pageSize, (pageNumber -1) * pageSize);
        List<PostFullDto> postsDto = postMapper.toListDto(posts);
        postsDto.forEach(post -> post.setComments(commentService.getPostComments(post.getId())));
        PagingParametersDto pagingParametersDto = PagingParametersDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .hasPrevious(pageNumber > 1)
                .hasNext(pageNumber < Math.ceilDiv(postRepository.getPostsCount(), pageSize))
                .build();
        return new PostsWithParamsDto(postsDto, search, pagingParametersDto);
    }

    @Transactional
    public PostFullDto savePost(PostDto post) {
        PostFullDto fullPost = getPostFullDtoById(postRepository.save(postMapper.toPost(post)));
        if (fullPost != null) {
            fullPost.setComments(commentService.getPostComments(post.getId()));
        }
        return fullPost;
    }

    @Transactional
    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }

    @Transactional
    public void editPostById(Long id, PostDto post) {
        postRepository.editById(id, postMapper.toPost(post));
    }

    @Transactional
    public void likePostById(Long id, boolean like) {
        int currentLikesCount = 0;
        Post post = getPostById(id);
        if (post != null) {
            currentLikesCount = post.getLikesCount();
        }
        postRepository.likeById(id, (like ? currentLikesCount + 1 : (currentLikesCount > 0 ? currentLikesCount - 1 : 0)));
    }

    public Post getPostById(Long id) {
        return postRepository.getById(id).orElse(new Post());
    }

    public byte[] getImage(Long id) {
        return postRepository.getById(id).orElse(new Post()).getImage();
    }

    public PostFullDto getPostFullDtoById(Long id) {
        PostFullDto post = postMapper.toDto(getPostById(id));
        if (post != null) {
            post.setComments(commentService.getPostComments(post.getId()));
        }
        return post;
    }

    public PostDto getPostDtoById(Long id) {
        return postMapper.toPostDto(getPostById(id));
    }
}