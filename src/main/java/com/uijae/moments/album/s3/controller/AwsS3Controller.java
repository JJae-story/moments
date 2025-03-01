package com.uijae.moments.album.s3.controller;

import com.uijae.moments.album.s3.service.AwsS3Service;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class AwsS3Controller {

  private final AwsS3Service awsS3Service;

  // 이미지 업로드
  @Operation(summary = "이미지 업로드", description = "여러 개의 파일을 업로드하고, 업로드된 이미지 ID 목록을 반환합니다.")
  @PostMapping("/upload")
  public ResponseEntity<List<Long>> uploadFiles(@RequestParam("files") List<MultipartFile> files,
      @RequestParam("albumId") Long albumId) {

    List<Long> imageIds = awsS3Service.uploadFiles(files, albumId);

    return ResponseEntity.ok(imageIds);
  }

  // 이미지 URL 조회
  @Operation(summary = "이미지 다운로드", description = "이미지 ID를 통해 S3 URL을 가져옵니다.")
  @GetMapping("/download")
  public ResponseEntity<String> getFileUrl(@RequestParam("imageId") Long imageId) {

    return ResponseEntity.ok(awsS3Service.getFileUrl(imageId));
  }

  // 이미지 삭제
  @Operation(summary = "이미지 삭제", description = "이미지 ID를 이용해 S3에서 파일을 삭제합니다.")
  @DeleteMapping("/delete")
  public ResponseEntity<Void> deleteFile(@RequestParam("imageId") Long imageId) {

    awsS3Service.deleteFile(imageId);

    return ResponseEntity.noContent().build();
  }
}
