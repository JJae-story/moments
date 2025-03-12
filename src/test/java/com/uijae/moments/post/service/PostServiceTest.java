package com.uijae.moments.post.service;

import static com.uijae.moments.common.exception.ErrorCode.ALBUM_NOT_FOUND;
import static com.uijae.moments.common.exception.ErrorCode.FILE_COUNT_EXCEEDED;
import static com.uijae.moments.common.exception.ErrorCode.FILE_IS_EMPTY;
import static com.uijae.moments.common.exception.ErrorCode.FILE_SIZE_EXCEEDED;
import static com.uijae.moments.common.exception.ErrorCode.INVALID_DATE_RANGE;
import static com.uijae.moments.common.exception.ErrorCode.INVALID_WEEK_SELECTION;
import static com.uijae.moments.common.exception.ErrorCode.POST_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.ArrayList;
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

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

  @Mock
  private AlbumRepository albumRepository;

  @Mock
  private AlbumImageRepository albumImageRepository;

  @Mock
  private PostRepository postRepository;

  @Mock
  private AwsS3Service awsS3Service;

  @InjectMocks
  private PostService postService;

  @Test
  @DisplayName("제목, 내용, 사진을 입력 받아서 게시글을 등록한다.")
  void createPost_success() {
    // given
    Long userId = 1L;
    Long groupId = 1L;
    Long albumId = 1L;

    List<MultipartFile> mockFiles = List.of(
        new MockMultipartFile("test1.jpg", "test1.jpg", "image/jpeg", new byte[]{1, 2, 3, 4}),
        new MockMultipartFile("test2.jpg", "test2.jpg", "image/jpeg", new byte[]{5, 6, 7, 8})
    );

    PostCreateRequestDto request = PostCreateRequestDto.builder()
        .title("Test Title")
        .content("Test Content")
        .files(mockFiles)
        .build();

    Album album = new Album();
    Post post = PostMapper.toEntity(request);

    List<AlbumImage> albumImages = List.of(
        new AlbumImage(1L, "https://s3-bucket/test1.jpg", album),
        new AlbumImage(2L, "https://s3-bucket/test2.jpg", album)
    );

    when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
    when(awsS3Service.uploadFiles(mockFiles, albumId)).thenReturn(List.of(1L, 2L));
    when(albumImageRepository.findAllById(anyList())).thenReturn(albumImages);
    when(postRepository.save(any(Post.class))).thenReturn(post);

    // when
    PostResponseDto response = postService.createPost(userId, groupId, albumId, request);

    // then
    assertEquals(request.getTitle(), response.getTitle());
    verify(albumRepository, times(1)).findById(albumId);
    verify(awsS3Service, times(1)).uploadFiles(anyList(), eq(albumId));
    verify(albumImageRepository, times(1)).findAllById(anyList());
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  @DisplayName("앨범이 존재하지 않으면, ALBUM_NOT_FOUND 예외가 발생해야 한다.")
  void createPost_fail_album_not_found() {
    // given
    Long userId = 1L;
    Long groupId = 1L;
    Long albumId = 1L;

    List<MultipartFile> mockFiles = List.of(
        new MockMultipartFile("test1.jpg", "test1.jpg", "image/jpeg", new byte[]{1, 2, 3, 4}),
        new MockMultipartFile("test2.jpg", "test2.jpg", "image/jpeg", new byte[]{5, 6, 7, 8})
    );

    PostCreateRequestDto request = PostCreateRequestDto.builder()
        .title("Test Title")
        .content("Test Content")
        .files(mockFiles)
        .build();

    when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

    // when & then
    CustomException e = assertThrows(CustomException.class,
        () -> postService.createPost(userId, groupId, albumId, request));

    assertEquals(ALBUM_NOT_FOUND, e.getErrorCode());
  }

  @Test
  @DisplayName("파일 개수가 10개를 초과하면, FILE_COUNT_EXCEEDED 예외가 발생해야 한다.")
  void createPost_fail_file_count_exceeded() {
    // given
    Long userId = 1L;
    Long groupId = 1L;
    Long albumId = 1L;

    List<MultipartFile> mockFiles = List.of(
        new MockMultipartFile("test1.jpg", "test1.jpg", "image/jpeg", new byte[]{1, 2, 3, 4}),
        new MockMultipartFile("test2.jpg", "test2.jpg", "image/jpeg", new byte[]{5, 6, 7, 8}),
        new MockMultipartFile("test3.jpg", "test3.jpg", "image/jpeg", new byte[]{5, 6, 7, 8}),
        new MockMultipartFile("test4.jpg", "test4.jpg", "image/jpeg", new byte[]{5, 6, 7, 8}),
        new MockMultipartFile("test5.jpg", "test5.jpg", "image/jpeg", new byte[]{5, 6, 7, 8}),
        new MockMultipartFile("test6.jpg", "test6.jpg", "image/jpeg", new byte[]{5, 6, 7, 8}),
        new MockMultipartFile("test7.jpg", "test7.jpg", "image/jpeg", new byte[]{5, 6, 7, 8}),
        new MockMultipartFile("test8.jpg", "test8.jpg", "image/jpeg", new byte[]{5, 6, 7, 8}),
        new MockMultipartFile("test9.jpg", "test9.jpg", "image/jpeg", new byte[]{5, 6, 7, 8}),
        new MockMultipartFile("test10.jpg", "test10.jpg", "image/jpeg", new byte[]{5, 6, 7, 8}),
        new MockMultipartFile("test11.jpg", "test11.jpg", "image/jpeg", new byte[]{5, 6, 7, 8})
    );

    PostCreateRequestDto request = PostCreateRequestDto.builder()
        .title("Test Title")
        .content("Test Content")
        .files(mockFiles)
        .build();

    when(albumRepository.findById(albumId)).thenReturn(Optional.of(new Album()));

    // when & then
    CustomException e = assertThrows(CustomException.class,
        () -> postService.createPost(userId, groupId, albumId, request));

    assertEquals(FILE_COUNT_EXCEEDED, e.getErrorCode());
  }

  @Test
  @DisplayName("빈 파일이 있으면, FILE_IS_EMPTY 예외가 발생해야 한다.")
  void createPost_fail_file_is_empty() {
    // given
    Long userId = 1L;
    Long groupId = 1L;
    Long albumId = 1L;

    List<MultipartFile> mockFiles = List.of(
        new MockMultipartFile("test1.jpg", "test1.jpg", "image/jpeg", new byte[]{1, 2, 3, 4}),
        new MockMultipartFile("test2.jpg", "test2.jpg", "image/jpeg", new byte[]{5, 6, 7, 8}),
        new MockMultipartFile("test3.jpg", "test3.jpg", "image/jpeg", new byte[0])
    );

    PostCreateRequestDto request = PostCreateRequestDto.builder()
        .title("Test Title")
        .content("Test Content")
        .files(mockFiles)
        .build();

    when(albumRepository.findById(albumId)).thenReturn(Optional.of(new Album()));

    // when & then
    CustomException e = assertThrows(CustomException.class,
        () -> postService.createPost(userId, groupId, albumId, request));

    assertEquals(FILE_IS_EMPTY, e.getErrorCode());
  }

  @Test
  @DisplayName("파일 크기가 10MB를 초과하면, FILE_SIZE_EXCEEDED 예외가 발생해야 한다.")
  void createPost_fail_file_size_exceeded() {
    // given
    Long userId = 1L;
    Long groupId = 1L;
    Long albumId = 1L;

    List<MultipartFile> mockFiles = List.of(
        new MockMultipartFile("test1.jpg", "test1.jpg", "image/jpeg", new byte[]{1, 2, 3, 4}),
        new MockMultipartFile("test2.jpg", "test2.jpg", "image/jpeg", new byte[]{5, 6, 7, 8}),
        new MockMultipartFile("test3.jpg", "test3.jpg", "image/jpeg", new byte[10_485_761])
    );

    PostCreateRequestDto request = PostCreateRequestDto.builder()
        .title("Test Title")
        .content("Test Content")
        .files(mockFiles)
        .build();

    when(albumRepository.findById(albumId)).thenReturn(Optional.of(new Album()));

    // when & then
    CustomException e = assertThrows(CustomException.class,
        () -> postService.createPost(userId, groupId, albumId, request));

    assertEquals(FILE_SIZE_EXCEEDED, e.getErrorCode());
  }

  @Test
  @DisplayName("수정할 데이터를 입력 받아, 게시글을 수정한다.")
  void updatePost_success() {
    // given
    Long userId = 1L;
    Long groupId = 1L;
    Long postId = 1L;
    Long albumId = 1L;

    List<Long> deleteImageIds = List.of(1L);

    List<MultipartFile> files = List.of(
        new MockMultipartFile("file1.jpg", "file1.jpg", "image/jpeg", new byte[]{1, 2, 3, 4})
    );

    PostUpdateRequestDto request = PostUpdateRequestDto.builder()
        .title("Updated Title")
        .files(files)
        .deleteImageIds(deleteImageIds)
        .build();

    Album album = new Album();

    AlbumImage albumImage1 = new AlbumImage();
    albumImage1.setId(1L);
    AlbumImage albumImage2 = new AlbumImage();
    albumImage2.setId(2L);

    PostImage postImage1 = new PostImage();
    postImage1.setId(1L);
    postImage1.setAlbumImage(albumImage1);

    PostImage postImage2 = new PostImage();
    postImage2.setId(2L);
    postImage2.setAlbumImage(albumImage2);

    List<PostImage> postImageIds = new ArrayList<>();
    postImageIds.add(postImage1);
    postImageIds.add(postImage2);

    Post post = Post.builder()
        .title("Created Title")
        .postImages(postImageIds)
        .build();

    when(postRepository.findById(postId)).thenReturn(Optional.of(post));
    when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
    doNothing().when(awsS3Service).deleteFile(anyLong());

    when(awsS3Service.uploadFiles(files, albumId)).thenReturn(List.of(3L));
    when(albumImageRepository.findAllById(List.of(3L)))
        .thenReturn(List.of(new AlbumImage(3L, "https://s3-bucket/file3.jpg", album)));

    PostResponseDto response = postService.updatePost(userId, groupId, postId, albumId, request);

    assertEquals("Updated Title", response.getTitle());
    assertEquals(2, post.getPostImages().size());
    verify(awsS3Service, times(1)).deleteFile(1L);
    verify(albumRepository, times(1)).findById(albumId);
    verify(postRepository, times(1)).findById(postId);
  }

  @Test
  @DisplayName("게시글이 존재하지 않으면, POST_NOT_FOUND 예외가 발생해야 한다.")
  void updatePost_fail_post_not_found() {
    // given
    Long userId = 1L;
    Long groupId = 1L;
    Long postId = 1L;
    Long albumId = 1L;

    List<Long> deleteImageIds = List.of(1L);

    List<MultipartFile> files = List.of(
        new MockMultipartFile("file1.jpg", "file1.jpg", "image/jpeg", new byte[]{1, 2, 3, 4})
    );

    PostUpdateRequestDto request = PostUpdateRequestDto.builder()
        .title("Updated Title")
        .files(files)
        .deleteImageIds(deleteImageIds)
        .build();

    when(postRepository.findById(postId)).thenReturn(Optional.empty());

    CustomException e = assertThrows(CustomException.class,
        () -> postService.updatePost(userId, groupId, postId, albumId, request));

    assertEquals(POST_NOT_FOUND, e.getErrorCode());
  }

  @Test
  @DisplayName("앨범이 존재하지 않으면, ALBUM_NOT_FOUND 예외가 발생해야 한다.")
  void updatePost_fail_album_not_found() {
    // given
    Long userId = 1L;
    Long groupId = 1L;
    Long postId = 1L;
    Long albumId = 1L;

    List<Long> deleteImageIds = List.of(1L);

    List<MultipartFile> files = List.of(
        new MockMultipartFile("file1.jpg", "file1.jpg", "image/jpeg", new byte[]{1, 2, 3, 4})
    );

    PostUpdateRequestDto request = PostUpdateRequestDto.builder()
        .title("Updated Title")
        .files(files)
        .deleteImageIds(deleteImageIds)
        .build();

    when(postRepository.findById(postId)).thenReturn(Optional.of(new Post()));
    when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

    CustomException e = assertThrows(CustomException.class,
        () -> postService.updatePost(userId, groupId, postId, albumId, request));

    assertEquals(ALBUM_NOT_FOUND, e.getErrorCode());
  }

  @Test
  @DisplayName("기존 + 추가하려는 파일 개수가 10개를 초과하면, FILE_COUNT_EXCEEDED 예외가 발생해야 한다.")
  void updatePost_fail_file_count_exceeded() {
    // given
    Long userId = 1L;
    Long groupId = 1L;
    Long albumId = 1L;
    Long postId = 1L;

    List<MultipartFile> mockFiles = List.of(
        new MockMultipartFile("test1.jpg", "test1.jpg", "image/jpeg", new byte[]{1, 2, 3, 4}),
        new MockMultipartFile("test2.jpg", "test2.jpg", "image/jpeg", new byte[]{5, 6, 7, 8}),
        new MockMultipartFile("test3.jpg", "test3.jpg", "image/jpeg", new byte[]{5, 6, 7, 8}),
        new MockMultipartFile("test6.jpg", "test6.jpg", "image/jpeg", new byte[]{5, 6, 7, 8}),
        new MockMultipartFile("test7.jpg", "test7.jpg", "image/jpeg", new byte[]{5, 6, 7, 8})
    );

    PostUpdateRequestDto request = PostUpdateRequestDto.builder()
        .title("Test Title")
        .content("Test Content")
        .files(mockFiles)
        .build();

    Post post = Post.builder()
        .postImages(List.of(
            new PostImage(),
            new PostImage(),
            new PostImage(),
            new PostImage(),
            new PostImage(),
            new PostImage()
        ))
        .build();

    when(postRepository.findById(postId)).thenReturn(Optional.of(post));
    when(albumRepository.findById(albumId)).thenReturn(Optional.of(new Album()));

    // when & then
    CustomException e = assertThrows(CustomException.class,
        () -> postService.updatePost(userId, groupId, postId, albumId, request));

    assertEquals(FILE_COUNT_EXCEEDED, e.getErrorCode());
  }

  @Test
  @DisplayName("빈 파일이 있으면, FILE_IS_EMPTY 예외가 발생해야 한다.")
  void updatePost_fail_file_is_empty() {
    // given
    Long userId = 1L;
    Long groupId = 1L;
    Long albumId = 1L;
    Long postId = 1L;

    List<MultipartFile> mockFiles = List.of(
        new MockMultipartFile("test1.jpg", "test1.jpg", "image/jpeg", new byte[]{1, 2, 3, 4}),
        new MockMultipartFile("test2.jpg", "test2.jpg", "image/jpeg", new byte[]{5, 6, 7, 8}),
        new MockMultipartFile("test3.jpg", "test3.jpg", "image/jpeg", new byte[0])
    );

    PostUpdateRequestDto request = PostUpdateRequestDto.builder()
        .title("Test Title")
        .content("Test Content")
        .files(mockFiles)
        .build();

    when(postRepository.findById(postId)).thenReturn(Optional.of(new Post()));
    when(albumRepository.findById(albumId)).thenReturn(Optional.of(new Album()));

    // when & then
    CustomException e = assertThrows(CustomException.class,
        () -> postService.updatePost(userId, groupId, postId, albumId, request));

    assertEquals(FILE_IS_EMPTY, e.getErrorCode());
  }

  @Test
  @DisplayName("파일 크기가 10MB를 초과하면, FILE_SIZE_EXCEEDED 예외가 발생해야 한다.")
  void updatePost_fail_file_size_exceeded() {
    // given
    Long userId = 1L;
    Long groupId = 1L;
    Long albumId = 1L;
    Long postId = 1L;

    List<MultipartFile> mockFiles = List.of(
        new MockMultipartFile("test1.jpg", "test1.jpg", "image/jpeg", new byte[]{1, 2, 3, 4}),
        new MockMultipartFile("test2.jpg", "test2.jpg", "image/jpeg", new byte[]{5, 6, 7, 8}),
        new MockMultipartFile("test3.jpg", "test3.jpg", "image/jpeg", new byte[10_485_761])
    );

    PostUpdateRequestDto request = PostUpdateRequestDto.builder()
        .title("Test Title")
        .content("Test Content")
        .files(mockFiles)
        .build();

    when(postRepository.findById(postId)).thenReturn(Optional.of(new Post()));
    when(albumRepository.findById(albumId)).thenReturn(Optional.of(new Album()));

    // when & then
    CustomException e = assertThrows(CustomException.class,
        () -> postService.updatePost(userId, groupId, postId, albumId, request));

    assertEquals(FILE_SIZE_EXCEEDED, e.getErrorCode());
  }

  @Test
  @DisplayName("날짜를 입력 받으면, 해당 범위 내에 게시글 정보를 가져온다.")
  void getPostsByDateRange_success() {
    // given
    Long userId = 1L;
    Long groupId = 1L;
    LocalDate startDate = LocalDate.now().minusDays(10);
    int weeks = 1;

    LocalDate endDate = startDate.plusWeeks(weeks);

    Post post1 = Post.builder()
        .id(1L)
        .title("Post 1")
        .createdDateTime(startDate.atStartOfDay())
        .build();

    Post post2 = Post.builder()
        .id(2L)
        .title("Post 2")
        .createdDateTime(startDate.plusDays(3).atStartOfDay())
        .build();

    List<Post> mockPosts = List.of(post1, post2);

    when(postRepository.findByCreatedDateTimeBetween(
        startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX))
    ).thenReturn(mockPosts);

    // when
    List<PostResponseDto> response = postService.getPostsByDateRange(userId, groupId, startDate,
        weeks);

    // then
    assertEquals(2, response.size());
    assertEquals("Post 1", response.get(0).getTitle());
    assertEquals("Post 2", response.get(1).getTitle());
    verify(postRepository, times(1)).findByCreatedDateTimeBetween(
        startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)
    );
  }

  @Test
  @DisplayName("현재 날짜보다 뒤에 날짜를 시작 지점으로 잡으면, INVALID_DATE_RANGE 예외가 발생해야 한다.")
  void getPostsByDateRange_fail_invalid_date_range() {
    // given
    Long userId = 1L;
    Long groupId = 1L;
    LocalDate startDate = LocalDate.now().plusDays(3);
    int weeks = 1;

    // when & then
    CustomException e = assertThrows(CustomException.class,
        () -> postService.getPostsByDateRange(userId, groupId, startDate, weeks));

    assertEquals(INVALID_DATE_RANGE, e.getErrorCode());
  }

  @Test
  @DisplayName("1 or 2주가 아닐 시에, INVALID_WEEK_SELECTION 예외가 발생해야 한다.")
  void getPostsByDateRange_fail_invalid_week_selection() {
    // given
    Long userId = 1L;
    Long groupId = 1L;
    LocalDate startDate = LocalDate.now().minusDays(10);
    int weeks = 3;

    // when & then
    CustomException e = assertThrows(CustomException.class,
        () -> postService.getPostsByDateRange(userId, groupId, startDate, weeks));

    assertEquals(INVALID_WEEK_SELECTION, e.getErrorCode());
  }

  @Test
  @DisplayName("게시글 아이디를 입력 받으면, 해당 게시글을 삭제한다.")
  void deletePost_success() {
    // given
    Long userId = 1L;
    Long groupId = 1L;
    Long postId = 1L;

    Post post = new Post();
    when(postRepository.findById(postId)).thenReturn(Optional.of(post));

    // when
    postService.deletePost(userId, groupId, postId);

    // then
    verify(postRepository, times(1)).findById(postId);
    verify(postRepository, times(1)).delete(post);
  }

  @Test
  @DisplayName("존재하지 않은 게시글 아이디를 입력 받으면, POST_NOT_FOUND 예외가 발생해야 한다.")
  void deletePost_fail_post_not_found() {
    // given
    Long userId = 1L;
    Long groupId = 1L;
    Long postId = 1L;

    when(postRepository.findById(postId)).thenReturn(Optional.empty());

    // when & then
    CustomException e = assertThrows(CustomException.class,
        () -> postService.deletePost(userId, groupId, postId));

    assertEquals(POST_NOT_FOUND, e.getErrorCode());
  }
}