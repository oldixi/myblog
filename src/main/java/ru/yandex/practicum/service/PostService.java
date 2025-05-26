package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.mapper.PostMapper;
import ru.yandex.practicum.model.PagingParameters;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.model.PostsWithParams;
import ru.yandex.practicum.repository.PostRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentService commentService;
    private final PostMapper postMapper;

    public PostsWithParams getPosts(String search, int pageNumber, int pageSize) {
        //Pageable page = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        List<Post> fullPosts = postRepository.getPosts(search, pageNumber * pageSize);
        List<Post> subPosts = fullPosts.subList((pageNumber - 1) * pageSize - 1, fullPosts.size() - 1);
        List<PostDto> posts = postMapper.toListDto(subPosts);
        posts.forEach(post -> post.setComments(commentService.getPostComments(post.getId())));
        PagingParameters pagingParameters = PagingParameters.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .hasPrevious(pageNumber > 1)
                .hasNext(pageNumber < Math.ceilDiv(posts.size(), pageSize))
                .build();
        return new PostsWithParams(posts, search, pagingParameters);
    }

    public void savePost(PostDto post) {
        postRepository.save(postMapper.to(post));
    }

    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }

    public void editPostById(Long id, PostDto post) {
        postRepository.editById(id, postMapper.to(post));
    }

    public void likePostById(Long id, boolean like) {
        Post post = getPostById(id);
        post.setLikesCount(like ? post.getLikesCount() + 1 : (post.getLikesCount() > 0 ? post.getLikesCount() - 1 : 0));
        postRepository.editById(id, post);
    }

    public Post getPostById(Long id) {
        return postRepository.getById(id).orElse(new Post());
    }

    public PostDto getPostDtoById(Long id) {
        return postMapper.toDto(getPostById(id));
    }
}