package com.uijae.moments.post.controller;

import com.uijae.moments.post.dto.PostCreateRequestDto;
import com.uijae.moments.post.dto.PostResponseDto;
import com.uijae.moments.post.dto.PostUpdateRequestDto;
import com.uijae.moments.post.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@RequestMapping
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;

  // 게시글 생성
  @PostMapping("/user/{userId}")
  public ResponseEntity<PostResponseDto> createPost(
      @PathVariable Long userId,
      @Valid @RequestBody PostCreateRequestDto postCreateRequestDto) {

    PostResponseDto response = postService.createPost(userId, postCreateRequestDto);

    return ResponseEntity.ok(response);
  }

  // 게시글 수정
  @PutMapping("/post/{postId}")
  public ResponseEntity<PostResponseDto> updatePost(
      @PathVariable Long postId,
      @RequestBody PostUpdateRequestDto postUpdateRequestDto) {

    PostResponseDto response = postService.updatePost(postId, postUpdateRequestDto);

    return ResponseEntity.ok(response);
  }

  // 게시글 조회 (1~2주 범위)
  @GetMapping("/user/{userId}/group/{groupId}/date-range")
  public ResponseEntity<List<PostResponseDto>> getPostsByDateRange(
      @PathVariable Long userId,
      @PathVariable Long groupId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @Min(1) @Max(2) int weeks) {

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
