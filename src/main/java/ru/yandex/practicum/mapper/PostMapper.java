package ru.yandex.practicum.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.dto.PostFullDto;
import ru.yandex.practicum.model.dto.PostDto;
import ru.yandex.practicum.model.entity.Post;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostMapper {
    private final ModelMapper mapper;

    public PostFullDto toDto(Post entity) {
        PostFullDto dto = mapper.map(entity, PostFullDto.class);
        if (entity.getText() != null && !entity.getText().isBlank())
            dto.setTextPreview(Arrays.stream(Arrays.stream(entity
                            .getText()
                            .split("\t")).limit(2)
                            .collect(Collectors.joining("\t"))
                            .split("\n")).limit(3).collect(Collectors.joining()));
        return dto;
    }

    public List<PostFullDto> toListDto(List<Post> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public Post toPost(PostDto dto) {
        Post post = mapper.map(dto, Post.class);
        try {
            if (dto.getImage() != null)
                post.setImage(dto.getImage().getBytes());
        } catch (IOException e) {
            return post;
        }
        return post;
    }

    public Post toPost(PostFullDto dto) {
        return mapper.map(dto, Post.class);
    }
}
