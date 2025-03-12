package com.uijae.moments.album.s3.service;

import static com.uijae.moments.common.exception.ErrorCode.ALBUM_NOT_FOUND;
import static com.uijae.moments.common.exception.ErrorCode.FILE_DELETE_FAILED;
import static com.uijae.moments.common.exception.ErrorCode.FILE_UPLOAD_FAILED;
import static com.uijae.moments.common.exception.ErrorCode.IMAGE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uijae.moments.album.entity.Album;
import com.uijae.moments.album.image.entity.AlbumImage;
import com.uijae.moments.album.image.repository.AlbumImageRepository;
import com.uijae.moments.album.repository.AlbumRepository;
import com.uijae.moments.common.exception.CustomException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


@ExtendWith(MockitoExtension.class)
class AwsS3ServiceTest {

  @Mock
  private S3Client s3Client;

  @Mock
  private AlbumImageRepository albumImageRepository;

  @Mock
  private AlbumRepository albumRepository;

  @InjectMocks
  private AwsS3Service awsS3Service;

  @Test
  @DisplayName("앨범 ID에 여러 개의 이미지를 업로드하면, 저장된 이미지 ID 목록을 반환해야 한다.")
  void uploadFiles_success() {
    // given
    List<MultipartFile> mockFiles = List.of(
        new MockMultipartFile("test1.jpg", "test1.jpg", "image/jpeg", new byte[]{1, 2, 3, 4}),
        new MockMultipartFile("test2.jpg", "test2.jpg", "image/jpeg", new byte[]{5, 6, 7, 8})
    );

    Album mockAlbum = Album.builder()
        .id(1L)
        .title("test album title")
        .summary("test album summary")
        .type("test type")
        .build();

    when(albumRepository.findById(anyLong())).thenReturn(Optional.of(mockAlbum));

    // Mock S3Client 동작
    when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(null);

    // Mock AlbumImage 저장 동작
    AlbumImage mockAlbumImage1 = new AlbumImage();
    mockAlbumImage1.setId(100L);
    mockAlbumImage1.setAlbum(mockAlbum);

    AlbumImage mockAlbumImage2 = new AlbumImage();
    mockAlbumImage2.setId(101L);
    mockAlbumImage2.setAlbum(mockAlbum);

    when(albumImageRepository.save(any(AlbumImage.class)))
        .thenReturn(mockAlbumImage1, mockAlbumImage2);

    // when
    List<Long> imageIds = awsS3Service.uploadFiles(mockFiles, 1L);

    // then
    assertNotNull(imageIds);
    assertEquals(2, imageIds.size());
    assertTrue(imageIds.contains(100L));
    assertTrue(imageIds.contains(101L));
    verify(albumImageRepository, times(2)).save(any(AlbumImage.class));
  }

  @Test
  @DisplayName("존재하지 않는 앨범 ID로 요청하면, ALBUM_NOT_FOUND 예외가 발생해야 한다.")
  void uploadFiles_fail_album_not_found() {
    // given
    List<MultipartFile> mockFiles = List.of(
        new MockMultipartFile("test1.jpg", "test1.jpg", "image/jpeg", new byte[]{1, 2, 3, 4}),
        new MockMultipartFile("test2.jpg", "test2.jpg", "image/jpeg", new byte[]{5, 6, 7, 8})
    );

    when(albumRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when & then
    CustomException e = assertThrows(CustomException.class,
        () -> awsS3Service.uploadFiles(mockFiles, 1L));

    assertEquals(ALBUM_NOT_FOUND, e.getErrorCode());
  }

  @Test
  @DisplayName("S3 업로드 중 오류가 발생하면, FILE_UPLOAD_FAILED 예외가 발생해야 한다.")
  void uploadFiles_fail_file_upload_failed() {
    List<MultipartFile> mockFiles = List.of(
        new MockMultipartFile("test1.jpg", "test1.jpg", "image/jpeg", new byte[]{1, 2, 3, 4}),
        new MockMultipartFile("test2.jpg", "test2.jpg", "image/jpeg", new byte[]{5, 6, 7, 8})
    );

    Album mockAlbum = Album.builder()
        .id(1L)
        .title("test album title")
        .summary("test album summary")
        .type("test type")
        .build();

    when(albumRepository.findById(anyLong())).thenReturn(Optional.of(mockAlbum));

    AlbumImage mockAlbumImage1 = new AlbumImage();
    mockAlbumImage1.setId(100L);
    mockAlbumImage1.setAlbum(mockAlbum);

    AlbumImage mockAlbumImage2 = new AlbumImage();
    mockAlbumImage2.setId(101L);
    mockAlbumImage2.setAlbum(mockAlbum);

    doThrow(new CustomException(FILE_UPLOAD_FAILED))
        .when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

    // when & then
    CustomException e = assertThrows(CustomException.class,
        () -> awsS3Service.uploadFiles(mockFiles, 1L));

    assertEquals(FILE_UPLOAD_FAILED, e.getErrorCode());
  }

  @Test
  @DisplayName("이미지 ID를 입력 받으면, DB에 저장된 이미지 URL을 반환해야 한다.")
  void getFileUrl_success() {
    Album mockAlbum = Album.builder()
        .id(1L)
        .title("test album title")
        .summary("test album summary")
        .type("test type")
        .build();

    AlbumImage mockAlbumImage = new AlbumImage();
    mockAlbumImage.setId(101L);
    mockAlbumImage.setUrl("https://fake.s3.amazonaws.com/test.jpg");
    mockAlbumImage.setAlbum(mockAlbum);

    when(albumImageRepository.findById(anyLong()))
        .thenReturn(Optional.of(mockAlbumImage));

    String url = awsS3Service.getFileUrl(mockAlbumImage.getId());

    assertNotNull(url);
    assertEquals("https://fake.s3.amazonaws.com/test.jpg", url);
  }

  @Test
  @DisplayName("존재하지 않는 이미지 ID를 조회하면, IMAGE_NOT_FOUND 예외가 발생해야 한다.")
  void getFileUrl_fail_image_not_found() {
    // given
    when(albumImageRepository.findById(anyLong()))
        .thenReturn(Optional.empty());

    // when & then
    CustomException e = assertThrows(CustomException.class,
        () -> awsS3Service.getFileUrl(999L));

    assertEquals(IMAGE_NOT_FOUND, e.getErrorCode());
  }

  @Test
  @DisplayName("이미지 ID를 입력 받으면, 해당 사진을 삭제해야 한다.")
  void deleteFile_success() {
    Album mockAlbum = Album.builder()
        .id(1L)
        .title("test album title")
        .summary("test album summary")
        .type("test type")
        .build();

    AlbumImage mockAlbumImage = new AlbumImage();
    mockAlbumImage.setId(101L);
    mockAlbumImage.setUrl("https://fake.s3.amazonaws.com/test.jpg");
    mockAlbumImage.setAlbum(mockAlbum);

    when(albumImageRepository.findById(anyLong()))
        .thenReturn(Optional.of(mockAlbumImage));

    // when
    awsS3Service.deleteFile(mockAlbumImage.getId());

    // then
    verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    verify(albumImageRepository, times(1)).delete(mockAlbumImage);
  }

  @Test
  @DisplayName("존재하지 않는 이미지 ID를 조회하면, IMAGE_NOT_FOUND 예외가 발생해야 한다.")
  void deleteFile_fail_image_not_found() {
    Long imageId = 333L;
    when(albumImageRepository.findById(imageId)).thenReturn(Optional.empty());

    // when & then
    CustomException e = assertThrows(CustomException.class, () -> awsS3Service.deleteFile(imageId));

    assertEquals(IMAGE_NOT_FOUND, e.getErrorCode());
    verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
    verify(albumImageRepository, never()).delete(any(AlbumImage.class));
  }

  @Test
  @DisplayName("S3 삭제 중 오류가 발생하면, FILE_DELETE_FAILED 예외가 발생해야 한다.")
  void deleteFile_fail_file_delete_failed() {
    Album mockAlbum = Album.builder()
        .id(1L)
        .title("test album title")
        .summary("test album summary")
        .type("test type")
        .build();

    AlbumImage mockAlbumImage = new AlbumImage();
    mockAlbumImage.setId(101L);
    mockAlbumImage.setUrl("https://fake.s3.amazonaws.com/test.jpg");
    mockAlbumImage.setAlbum(mockAlbum);

    when(albumImageRepository.findById(anyLong()))
        .thenReturn(Optional.of(mockAlbumImage));

    doThrow(new CustomException(FILE_DELETE_FAILED))
        .when(s3Client).deleteObject(any(DeleteObjectRequest.class));

    // when & then
    CustomException e = assertThrows(CustomException.class,
        () -> awsS3Service.deleteFile(101L));

    assertEquals(FILE_DELETE_FAILED, e.getErrorCode());
  }
}