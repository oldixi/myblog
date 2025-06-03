package ru.yandex.practicum.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.dto.PostFullDto;
import ru.yandex.practicum.model.dto.PostDto;
import ru.yandex.practicum.model.entity.Post;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostMapper {
    private final ModelMapper mapper;

    @Value("${image.path}")
    private String imagePath;

    public PostFullDto toDto(Post entity) {
        PostFullDto dto = mapper.map(entity, PostFullDto.class);
        if (dto != null) {
            if (entity.getText() != null && !entity.getText().isBlank())
                dto.setText(Arrays.stream(entity.getText().split("\n")).toList());
            if (entity.getTags() != null && !entity.getTags().isBlank())
                dto.setTags(Arrays.stream(entity.getTags().split(",|, | ")).toList());
            dto.setImagePath(imagePath + dto.getId());
        }
        return dto;
    }

    public List<PostFullDto> toListDto(List<Post> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public Post toPost(PostDto dto) {
        Post post = mapper.map(dto, Post.class);
        if (post != null) {
            try {
                if (dto.getImage() != null && !dto.getImage().isEmpty())
                    post.setImage(dto.getImage().getBytes());
            } catch (IOException e) {
                return post;
            }
        }
        return post;
    }

    public PostDto toPostDto(Post post) {
        return mapper.map(post, PostDto.class);
    }
}
