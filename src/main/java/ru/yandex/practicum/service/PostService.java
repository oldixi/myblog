package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final PostMapper postMapper;

    public PostsWithParamsDto getPosts(String search, int pageNumber, int pageSize) {
        final Pageable page = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        Page<Post> posts;
        if (search != null && !search.isBlank())
            posts = postRepository.getPostsByTagsLike(search, page);
        else
            posts = postRepository.findAll(page);
        List<PostFullDto> postsDto = postMapper.toListDto(posts);

        PagingParametersDto pagingParametersDto = PagingParametersDto.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .hasPrevious(pageNumber > 1)
                .hasNext(pageNumber < Math.ceilDiv(postRepository.count(), pageSize))
                .build();
        return new PostsWithParamsDto(postsDto, search, pagingParametersDto);
    }

    @Transactional
    public PostFullDto savePost(PostDto post) {
        return postMapper.toDto(postRepository.save(postMapper.toPost(post)));
    }

    @Transactional
    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }

    @Transactional
    public void editPostById(Long id, PostDto post) {
        if (post.getId() != null) {
            if (post.getImage() == null || post.getImage().isEmpty())
                postRepository.editByIdWithoutImage(id, post.getTitle(), post.getText(), post.getTags());
            else {
                postRepository.save(postMapper.toPost(post, getPostById(id).getLikesCount()));
            }
        }
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
        return postRepository.findById(id).orElse(new Post());
    }

    public byte[] getImage(Long id) {
        return postRepository.findById(id).orElse(new Post()).getImage();
    }

    public PostFullDto getPostFullDtoById(Long id) {
        return postMapper.toDto(getPostById(id));
    }

    public PostDto getPostDtoById(Long id) {
        return postMapper.toPostDto(getPostById(id));
    }
}