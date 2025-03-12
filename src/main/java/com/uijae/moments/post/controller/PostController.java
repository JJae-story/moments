package com.uijae.moments.post.controller;

import com.uijae.moments.post.dto.PostCreateRequestDto;
import com.uijae.moments.post.dto.PostResponseDto;
import com.uijae.moments.post.dto.PostUpdateRequestDto;
import com.uijae.moments.post.service.PostService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;

  // 게시글 생성
  @PostMapping("/user/{userId}/group/{groupId}/album/{albumId}")
  public ResponseEntity<PostResponseDto> createPost(
      @PathVariable Long userId,
      @PathVariable Long groupId,
      @PathVariable Long albumId,
      @Valid @RequestBody PostCreateRequestDto postCreateRequestDto) {

    PostResponseDto response = postService.createPost(userId, groupId, albumId,
        postCreateRequestDto);

    return ResponseEntity.ok(response);
  }

  // 게시글 수정
  @PutMapping("/user/{userId}/group/{groupId}/post/{postId}/album/{albumId}")
  public ResponseEntity<PostResponseDto> updatePost(
      @PathVariable Long userId,
      @PathVariable Long groupId,
      @PathVariable Long postId,
      @PathVariable Long albumId,
      @RequestBody PostUpdateRequestDto postUpdateRequestDto) {

    PostResponseDto response = postService.updatePost(userId, groupId, postId, albumId,
        postUpdateRequestDto);

    return ResponseEntity.ok(response);
  }

  // 게시글 조회 (1~2주 범위)
  @GetMapping("/user/{userId}/group/{groupId}/date-range")
  public ResponseEntity<List<PostResponseDto>> getPostsByDateRange(
      @PathVariable Long userId,
      @PathVariable Long groupId,
      @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam("weeks") int weeks) {

    List<PostResponseDto> response = postService.getPostsByDateRange(userId, groupId, startDate,
        weeks);

    return ResponseEntity.ok(response);
  }

  // 게시글 삭제
  @DeleteMapping("/user/{userId}/group/{groupId}/post/{postId}")
  public ResponseEntity<Void> deletePost(
      @PathVariable Long userId,
      @PathVariable Long groupId,
      @PathVariable Long postId) {

    postService.deletePost(userId, groupId, postId);

    return ResponseEntity.noContent().build();
  }

}
