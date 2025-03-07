package com.uijae.moments.album.service;

import static com.uijae.moments.common.exception.ErrorCode.ALBUM_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uijae.moments.album.dto.AlbumCreateRequestDto;
import com.uijae.moments.album.dto.AlbumResponseDto;
import com.uijae.moments.album.dto.AlbumUpdateRequestDto;
import com.uijae.moments.album.entity.Album;
import com.uijae.moments.album.repository.AlbumRepository;
import com.uijae.moments.album.util.AlbumMapper;
import com.uijae.moments.common.exception.CustomException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

  @Mock
  private AlbumRepository albumRepository;

  @InjectMocks
  private AlbumService albumService;

  @Test
  @DisplayName("앨범 생성이 성공하면, 생성된 앨범 정보를 반환해야 한다.")
  void createAlbum_success() {
    // given
    AlbumCreateRequestDto requestDto = AlbumCreateRequestDto.builder()
        .title("새로운 앨범")
        .type("여행")
        .summary("새로운 여행 기록")
        .build();

    Album createdAlbum = AlbumMapper.toEntity(requestDto);

    when(albumRepository.save(any(Album.class))).thenReturn(createdAlbum);

    // when
    AlbumResponseDto response = albumService.createAlbum(requestDto);

    // then
    assertEquals("새로운 앨범", response.getTitle());
    assertEquals("여행", response.getType());
    assertEquals("새로운 여행 기록", response.getSummary());
    verify(albumRepository, times(1)).save(any(Album.class));
  }

  @Test
  @DisplayName("그룹 ID로 앨범을 조회하면, 해당 그룹의 모든 앨범을 반환해야 한다.")
  void findAlbumsByGroup_success() {
    // given
    Long userId = 1L;
    Long groupId = 100L;

    List<Album> albums = List.of(
        Album.builder().id(1L).title("신나는 여행").type("여행").summary("여행 일지").build(),
        Album.builder().id(2L).title("고양이 사진 모음").type("동물").summary("고양이 사진 모음").build()
    );

    when(albumRepository.findByGroupId(groupId)).thenReturn(albums);

    // when
    List<AlbumResponseDto> response = albumService.findAlbumsByGroup(userId, groupId);

    // then
    assertNotNull(response);
    assertEquals(2, response.size());
    assertEquals("여행 일지", response.get(0).getSummary());
    assertEquals("고양이 사진 모음", response.get(1).getTitle());
    verify(albumRepository, times(1)).findByGroupId(groupId);
  }

  @Test
  @DisplayName("앨범 수정이 성공하면, 수정된 정보를 반환해야 한다.")
  void updateAlbum_success() {
    // given
    Long albumId = 1L;

    Album album = Album.builder()
        .id(albumId)
        .title("완전 재밌는 일상")
        .type("일상")
        .summary("재밌는 일상 기록")
        .build();

    AlbumUpdateRequestDto updateRequest = AlbumUpdateRequestDto.builder()
        .title("일상 앨범")
        .type("일상")
        .summary("일상 기록")
        .build();

    when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
    when(albumRepository.save(any(Album.class))).thenReturn(album);

    // when
    AlbumResponseDto response = albumService.updateAlbum(albumId, updateRequest);

    // then
    assertEquals("일상 앨범", response.getTitle());
    assertEquals("일상", response.getType());
    assertEquals("일상 기록", response.getSummary());
    verify(albumRepository, times(1)).findById(albumId);
    verify(albumRepository, times(1)).save(any(Album.class));
  }

  @Test
  @DisplayName("존재하지 않는 앨범을 수정하려 하면, ALBUM_NOT_FOUND 예외가 발생해야 한다.")
  void updateAlbum_fail_album_not_found() {
    // given
    Long albumId = 1L;
    AlbumUpdateRequestDto updateRequest = AlbumUpdateRequestDto.builder()
        .title("없는 앨범")
        .type("없음")
        .summary("없음")
        .build();

    when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

    // when & then
    CustomException e = assertThrows(CustomException.class,
        () -> albumService.updateAlbum(albumId, updateRequest));

    assertEquals(ALBUM_NOT_FOUND, e.getErrorCode());
    verify(albumRepository, times(1)).findById(albumId);
  }

  @Test
  @DisplayName("앨범 삭제가 성공하면, 앨범이 정상적으로 삭제되야 한다.")
  void deleteAlbum_success() {
    // given
    Long albumId = 1L;
    Long userId = 1L;
    Long groupId = 100L;

    Album album = Album.builder()
        .id(albumId)
        .title("완전 재밌는 일상")
        .type("일상")
        .summary("재밌는 일상 기록")
        .build();

    when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
    doNothing().when(albumRepository).deleteById(albumId);

    // when
    albumService.deleteAlbum(albumId, userId, groupId);

    // then
    verify(albumRepository, times(1)).findById(albumId);
    verify(albumRepository, times(1)).deleteById(albumId);
  }

  @Test
  @DisplayName("존재하지 않는 앨범을 삭제하려 하면, ALBUM_NOT_FOUND 예외가 발생해야 한다.")
  void deleteAlbum_fail_album_not_found() {
    // Given
    Long albumId = 1L;
    Long userId = 1L;
    Long groupId = 100L;

    when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

    // When & Then
    CustomException e = assertThrows(CustomException.class,
        () -> albumService.deleteAlbum(albumId, userId, groupId));

    assertEquals(ALBUM_NOT_FOUND, e.getErrorCode());
    verify(albumRepository, times(1)).findById(albumId);
  }
}