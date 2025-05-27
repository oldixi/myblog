package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentService commentService;
    private final PostMapper postMapper;

    public PostsWithParamsDto getPosts(String search, int pageNumber, int pageSize) {
        //Pageable page = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        List<Post> fullPosts = postRepository.getPosts(search, pageNumber * pageSize);
        List<Post> subPosts = fullPosts.subList((pageNumber - 1) * pageSize - 1, fullPosts.size() - 1);
        List<PostFullDto> posts = postMapper.toListDto(subPosts);
        posts.forEach(post -> post.setComments(commentService.getPostComments(post.getId())));
        PagingParametersDto pagingParametersDto = PagingParametersDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .hasPrevious(pageNumber > 1)
                .hasNext(pageNumber < Math.ceilDiv(posts.size(), pageSize))
                .build();
        return new PostsWithParamsDto(posts, search, pagingParametersDto);
    }

    @Transactional
    public Post savePost(PostDto post) {
        return postRepository.getById(postRepository.save(postMapper.toPost(post))).orElse(new Post());
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
        Post post = getPostById(id);
        int currentLikesCount = post.getLikesCount();
        postRepository.likeById(id, (like ? currentLikesCount + 1 : (currentLikesCount > 0 ? currentLikesCount - 1 : 0)));
    }

    public Post getPostById(Long id) {
        return postRepository.getById(id).orElse(new Post());
    }

    public byte[] getImage(Long id) {
        return postRepository.getById(id).orElse(new Post()).getImage();
    }

    public PostFullDto getPostDtoById(Long id) {
        return postMapper.toDto(getPostById(id));
    }
}