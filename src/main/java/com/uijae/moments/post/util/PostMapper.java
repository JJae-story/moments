package com.uijae.moments.post.util;

import com.uijae.moments.post.dto.PostCreateRequestDto;
import com.uijae.moments.post.dto.PostResponseDto;
import com.uijae.moments.post.entity.Post;

public class PostMapper {

  // DTO -> Entity 변환
  public static Post toEntity(PostCreateRequestDto postCreateRequestDto) {
    return Post.builder()
        .title(postCreateRequestDto.getTitle())
        .content(postCreateRequestDto.getContent())
        .build();
  }

  // Entity -> DTO 변환
  public static PostResponseDto toResponseDto(Post post) {
    return PostResponseDto.builder()
        .title(post.getTitle())
        .createdDateTime(post.getCreatedDateTime())
        .build();
  }

}
