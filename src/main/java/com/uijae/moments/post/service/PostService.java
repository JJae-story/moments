package com.uijae.moments.post.service;

import static com.uijae.moments.common.exception.ErrorCode.ALBUM_NOT_FOUND;
import static com.uijae.moments.common.exception.ErrorCode.FILE_COUNT_EXCEEDED;
import static com.uijae.moments.common.exception.ErrorCode.FILE_IS_EMPTY;
import static com.uijae.moments.common.exception.ErrorCode.FILE_SIZE_EXCEEDED;
import static com.uijae.moments.common.exception.ErrorCode.INVALID_DATE_RANGE;
import static com.uijae.moments.common.exception.ErrorCode.POST_NOT_FOUND;

import com.uijae.moments.album.entity.Album;
import com.uijae.moments.album.image.entity.AlbumImage;
import com.uijae.moments.album.image.repository.AlbumImageRepository;
import com.uijae.moments.album.repository.AlbumRepository;
import com.uijae.moments.album.s3.service.AwsS3Service;
import com.uijae.moments.common.exception.CustomException;
import com.uijae.moments.post.dto.PostCreateRequestDto;
import com.uijae.moments.post.dto.PostResponseDto;
import com.uijae.moments.post.dto.PostUpdateRequestDto;
import com.uijae.moments.post.entity.Post;
import com.uijae.moments.post.image.entity.PostImage;
import com.uijae.moments.post.repository.PostRepository;
import com.uijae.moments.post.util.PostMapper;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostService {

  private final AlbumRepository albumRepository;
  private final AlbumImageRepository albumImageRepository;
  private final PostRepository postRepository;
  private final AwsS3Service awsS3Service;

  /**
   * 공통 예외 처리 추가 예정 - 회원가입 여부 확인, 그룹이 존재하는지, 그룹이 존재한다면 그룹원이 맞는지 추가 후에 테스트 추가 작성 예정
   */

  // 게시글 등록
  @Transactional
  public PostResponseDto createPost(Long userId, PostCreateRequestDto postCreateRequestDto) {

    Album album = albumRepository.findById(postCreateRequestDto.getAlbumId())
        .orElseThrow(() -> new CustomException(ALBUM_NOT_FOUND));

    validateFileCount(0, postCreateRequestDto.getFiles().size());

    Post post = PostMapper.toEntity(postCreateRequestDto);

    if (fileExist(postCreateRequestDto.getFiles())) {
      List<AlbumImage> albumImages = uploadAndFetchAlbumImages(postCreateRequestDto.getFiles(),
          postCreateRequestDto.getAlbumId());

      for (AlbumImage albumImage : albumImages) {
        PostImage postImage = new PostImage();
        postImage.setAlbumImage(albumImage);
        post.addImage(postImage);
      }
    }

    postRepository.save(post);

    return PostMapper.toResponseDto(post);
  }

  /**
   * 예외처리 추가 예정 - 게시글 작성자가 맞는지 확인
   */

  // 게시글 수정
  @Transactional
  public PostResponseDto updatePost(Long postId, PostUpdateRequestDto postUpdateRequestDto) {

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

    Album album = albumRepository.findById(postUpdateRequestDto.getAlbumId())
        .orElseThrow(() -> new CustomException(ALBUM_NOT_FOUND));

    if (postUpdateRequestDto.getTitle() != null) {
      post.setTitle(postUpdateRequestDto.getTitle());
    }

    if (postUpdateRequestDto.getContent() != null) {
      post.setContent(postUpdateRequestDto.getContent());
    }

    // 삭제할 이미지 처리
    if (!CollectionUtils.isEmpty(postUpdateRequestDto.getDeleteImageIds())) {

      Set<Long> deletedPostImageIds = post.getPostImages().stream()
          .filter(
              it -> postUpdateRequestDto.getDeleteImageIds().contains(it.getAlbumImage().getId()))
          .map(it -> {
            awsS3Service.deleteFile(it.getAlbumImage().getId());
            return it.getId();
          })
          .collect(Collectors.toSet());

      post.setPostImages(
          post.getPostImages().stream()
              .filter(it -> !deletedPostImageIds.contains(it.getId()))
              .collect(Collectors.toList())
      );
    }

    // 현재 남아있는 이미지 개수 확인 후 검증
    int remainingImageFileCount = post.getPostImages().size();
    int newImageFileCount =
        (postUpdateRequestDto.getFiles() == null) ? 0 : postUpdateRequestDto.getFiles().size();
    validateFileCount(remainingImageFileCount, newImageFileCount);

    // 새로운 이미지 추가
    if (fileExist(postUpdateRequestDto.getFiles())) {
      List<AlbumImage> albumImages = uploadAndFetchAlbumImages(postUpdateRequestDto.getFiles(),
          postUpdateRequestDto.getAlbumId());

      for (AlbumImage albumImage : albumImages) {
        PostImage postImage = new PostImage();
        postImage.setAlbumImage(albumImage);
        post.addImage(postImage);
      }
    }

    return PostMapper.toResponseDto(post);
  }

  /**
   * Elasticsearch 적용 할 때, 키워드 검색 기능 추가 예정
   */

  // 게시글 조회 (1-2주 범위)
  @Transactional(readOnly = true)
  public List<PostResponseDto> getPostsByDateRange(Long userId, Long groupId,
      LocalDate startDate, int weeks) {

    LocalDate today = LocalDate.now();
    if (startDate.isAfter(today)) {
      throw new CustomException(INVALID_DATE_RANGE);
    }

    LocalDate endDate = startDate.plusWeeks(weeks);

    List<Post> posts = postRepository.findByCreatedDateTimeBetween(
        startDate.atStartOfDay(),
        endDate.atTime(LocalTime.MAX)
    );

    return posts.stream().map(PostMapper::toResponseDto).toList();
  }

  /**
   * 예외처리 추가 예정 - 게시글 작성자가 맞는지 확인
   */

  // 게시글 삭제
  @Transactional
  public void deletePost(Long userId, Long groupId, Long postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

    List<PostImage> postImages = post.getPostImages();

    if (!postImages.isEmpty()) {
      for (PostImage postImage : postImages) {
        awsS3Service.deleteFile(postImage.getAlbumImage().getId());
      }
    }

    postRepository.delete(post);
  }

  private boolean fileExist(List<MultipartFile> files) {
    if (files == null || files.isEmpty()) {
      return false;
    }

    for (MultipartFile file : files) {
      if (file.isEmpty()) {
        throw new CustomException(FILE_IS_EMPTY);
      }
      if (file.getSize() > (10 * 1024 * 1024)) {
        throw new CustomException(FILE_SIZE_EXCEEDED);
      }
    }

    return true;
  }

  private void validateFileCount(int existingCount, int newCount) {
    int maxImageFilesAllowed = 10;

    if (existingCount + newCount > maxImageFilesAllowed) {
      throw new CustomException(FILE_COUNT_EXCEEDED);
    }
  }

  private List<AlbumImage> uploadAndFetchAlbumImages(List<MultipartFile> files, Long albumId) {
    List<Long> albumImageIds = awsS3Service.uploadFiles(files, albumId);
    return albumImageRepository.findAllById(albumImageIds);
  }
}
