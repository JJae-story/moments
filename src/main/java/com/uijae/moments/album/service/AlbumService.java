package com.uijae.moments.album.service;

import static com.uijae.moments.common.exception.ErrorCode.ALBUM_NOT_FOUND;

import com.uijae.moments.album.dto.AlbumCreateRequestDto;
import com.uijae.moments.album.dto.AlbumResponseDto;
import com.uijae.moments.album.dto.AlbumUpdateRequestDto;
import com.uijae.moments.album.entity.Album;
import com.uijae.moments.album.repository.AlbumRepository;
import com.uijae.moments.album.util.AlbumMapper;
import com.uijae.moments.common.exception.CustomException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlbumService {

  private final AlbumRepository albumRepository;

  /**
   * 공통 예외 처리 추가 예정 - 회원가입 여부 확인, 그룹이 존재하는지, 그룹이 존재한다면 그룹원이 맞는지 추가 후에 테스트 추가 작성 예정
   */

  // 앨범 생성
  @Transactional
  public AlbumResponseDto createAlbum(AlbumCreateRequestDto albumCreateRequestDto) {
    Album album = AlbumMapper.toEntity(albumCreateRequestDto);
    albumRepository.save(album);

    return AlbumMapper.toResponseDto(album);
  }

  // 앨범 조회
  @Transactional(readOnly = true)
  public List<AlbumResponseDto> findAlbumsByGroup(Long userId, Long groupId) {
    List<Album> albums = albumRepository.findByGroupId(groupId);

    List<AlbumResponseDto> responseList = new ArrayList<>();

    for (Album album : albums) {
      AlbumResponseDto dto = AlbumMapper.toResponseDto(album);
      responseList.add(dto);
    }

    return responseList;
  }

  // 앨범 수정
  @Transactional
  public AlbumResponseDto updateAlbum(Long albumId, AlbumUpdateRequestDto albumUpdateRequestDto) {
    Album album = albumRepository.findById(albumId)
        .orElseThrow(() -> new CustomException(ALBUM_NOT_FOUND));

    if (albumUpdateRequestDto.getTitle() != null) {
      album.setTitle(albumUpdateRequestDto.getTitle());
    }
    if (albumUpdateRequestDto.getType() != null) {
      album.setType(albumUpdateRequestDto.getType());
    }
    if (albumUpdateRequestDto.getSummary() != null) {
      album.setSummary(albumUpdateRequestDto.getSummary());
    }

    return AlbumMapper.toResponseDto(album);
  }


  /**
   * 추가 예외 처리 예정 - 삭제하려는 앨범이 그룹에 속해 있는지 확인
   */

  // 앨범 삭제
  @Transactional
  public void deleteAlbum(Long albumId, Long userId, Long groupId) {
    Album album = albumRepository.findById(albumId)
        .orElseThrow(() -> new CustomException(ALBUM_NOT_FOUND));

    albumRepository.delete(album);
  }
}
