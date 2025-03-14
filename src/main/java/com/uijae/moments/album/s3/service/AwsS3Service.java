package com.uijae.moments.album.s3.service;

import static com.uijae.moments.common.exception.ErrorCode.ALBUM_NOT_FOUND;
import static com.uijae.moments.common.exception.ErrorCode.FILE_DELETE_FAILED;
import static com.uijae.moments.common.exception.ErrorCode.FILE_UPLOAD_FAILED;
import static com.uijae.moments.common.exception.ErrorCode.IMAGE_NOT_FOUND;

import com.uijae.moments.album.entity.Album;
import com.uijae.moments.album.image.entity.AlbumImage;
import com.uijae.moments.album.image.repository.AlbumImageRepository;
import com.uijae.moments.album.repository.AlbumRepository;
import com.uijae.moments.common.exception.CustomException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3Service {

  private final S3Client s3Client;
  private final AlbumImageRepository albumImageRepository;
  private final AlbumRepository albumRepository;

  @Value("${aws.s3.bucket-name}")
  private String bucketName;

  // 여러 개의 파일 업로드
  public List<Long> uploadFiles(List<MultipartFile> files, Long albumId) {
    log.info("파일 업로드 요청 - albumId: {}, 파일 개수: {}", albumId, files.size());

    Album album = albumRepository.findById(albumId)
        .orElseThrow(() -> new CustomException(ALBUM_NOT_FOUND));

    List<Long> imageIds = new ArrayList<>();

    for (MultipartFile file : files) {

      String filename = file.getOriginalFilename();
      String s3Key = UUID.randomUUID() + "_" + filename;

      // S3에 업로드
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(s3Key)
          .build();

      try {
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
      } catch (IOException e) {
        log.error("파일 업로드 실패 - S3에 업로드하는 중 오류 발생", e);
        throw new CustomException(FILE_UPLOAD_FAILED);
      }

      String fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, s3Key);
      log.info("S3 업로드 성공 - URL: {}", fileUrl);

      // DB 데이터 저장
      AlbumImage albumImage = AlbumImage.builder()
          .url(fileUrl)
          .album(album)
          .build();

      AlbumImage savedImage = albumImageRepository.save(albumImage);
      log.info("DB 저장 완료 - imageId: {}", savedImage.getId());

      imageIds.add(savedImage.getId());
    }

    return imageIds;
  }

  // ID로 파일 URL 조회
  public String getFileUrl(Long imageId) {
    AlbumImage albumImage = getImage(imageId);

    return albumImage.getUrl();
  }

  // ID로 파일 삭제
  public void deleteFile(Long imageId) {
    AlbumImage albumImage = getImage(imageId);

    String fileKey = albumImage.getUrl().substring(albumImage.getUrl().lastIndexOf("/") + 1);

    try {
      // S3에서 삭제
      s3Client.deleteObject(DeleteObjectRequest.builder()
          .bucket(bucketName)
          .key(fileKey)
          .build());
    } catch (S3Exception e) {
      log.error("파일 삭제 실패 - S3에서 삭제하는 중 오류 발생", e);
      throw new CustomException(FILE_DELETE_FAILED);
    }

    // DB 데이터 삭제
    albumImageRepository.delete(albumImage);
    log.info("이미지 삭제 완료 - imageId: {}, fileKey: {}", imageId, fileKey);
  }

  private AlbumImage getImage(Long imageId) {
    return albumImageRepository.findById(imageId)
        .orElseThrow(() -> {
          log.error("이미지를 찾을 수 없음 - imageId: {}", imageId);
          return new CustomException(IMAGE_NOT_FOUND);
        });
  }
}
