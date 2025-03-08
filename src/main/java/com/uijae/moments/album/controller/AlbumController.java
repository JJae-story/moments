package com.uijae.moments.album.controller;

import com.uijae.moments.album.dto.AlbumCreateRequestDto;
import com.uijae.moments.album.dto.AlbumResponseDto;
import com.uijae.moments.album.dto.AlbumUpdateRequestDto;
import com.uijae.moments.album.service.AlbumService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/album")
@RequiredArgsConstructor
public class AlbumController {

  private final AlbumService albumService;

  // 앨범 생성
  @PostMapping
  public ResponseEntity<AlbumResponseDto> createAlbum(
      @Valid @RequestBody AlbumCreateRequestDto albumCreateRequestDto) {

    AlbumResponseDto response = albumService.createAlbum(albumCreateRequestDto);

    return ResponseEntity.ok(response);
  }

  // 앨범 조회
  @GetMapping("/user/{userId}/group/{groupId}")
  public ResponseEntity<List<AlbumResponseDto>> findAlbumsByGroup(
      @PathVariable Long userId, @PathVariable Long groupId) {

    List<AlbumResponseDto> response = albumService.findAlbumsByGroup(userId, groupId);

    return ResponseEntity.ok(response);
  }

  // 앨범 수정
  @PutMapping("/{albumId}")
  public ResponseEntity<AlbumResponseDto> updateAlbum(
      @PathVariable Long albumId,
      @Valid @RequestBody AlbumUpdateRequestDto albumUpdateRequestDto) {

    AlbumResponseDto response = albumService.updateAlbum(albumId, albumUpdateRequestDto);

    return ResponseEntity.ok(response);
  }

  // 앨범 삭제
  @DeleteMapping("/album/{albumId}/user/{userId}/group/{groupId}")
  public ResponseEntity<Void> deleteAlbum(
      @PathVariable Long albumId, @PathVariable Long userId, @PathVariable Long groupId) {

    albumService.deleteAlbum(albumId, userId, groupId);

    return ResponseEntity.noContent().build();
  }
}
