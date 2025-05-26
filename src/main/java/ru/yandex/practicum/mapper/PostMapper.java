package ru.yandex.practicum.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.PostDto;
import ru.yandex.practicum.model.Post;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostMapper {
    private final ModelMapper mapper;

    public PostDto toDto(Post entity) {
        PostDto dto = mapper.map(entity, PostDto.class);
        if (entity.getImage() != null)
            dto.setImagePath("/post_" + entity.getId() + "_image");
        return dto;
    }

    public List<PostDto> toListDto(List<Post> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public Post to(PostDto dto) {
        Post post = mapper.map(dto, Post.class);
        BufferedImage image;
        try {
            image = ImageIO.read(new File(dto.getImagePath()));
        } catch (IOException e) {
            return post;
        }
        if (image != null) {
            post.setImage(image);
        }
        return post;
    }
}
