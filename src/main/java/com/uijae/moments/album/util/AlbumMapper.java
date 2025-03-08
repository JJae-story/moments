package com.uijae.moments.album.util;

import com.uijae.moments.album.dto.AlbumCreateRequestDto;
import com.uijae.moments.album.dto.AlbumResponseDto;
import com.uijae.moments.album.entity.Album;

public class AlbumMapper {

  // DTO -> Entity 변환
  public static Album toEntity(AlbumCreateRequestDto albumCreateRequestDto) {
    Album album = new Album();
    album.setTitle(albumCreateRequestDto.getTitle());
    album.setType(albumCreateRequestDto.getType());
    album.setSummary(albumCreateRequestDto.getSummary());

    return album;
  }

  // Entity -> DTO 변환
  public static AlbumResponseDto toResponseDto(Album album) {
    return AlbumResponseDto.builder()
        .title(album.getTitle())
        .type(album.getType())
        .summary(album.getSummary())
        .createdDate(album.getCreatedDate())
        .build();
  }
}
