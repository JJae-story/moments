package com.uijae.moments.post.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class PostUpdateRequestDto {

  private Long userId;
  private Long groupId;
  private Long albumId;

  private String title;
  private String content;
  private List<MultipartFile> files;
  private List<Long> deleteImageIds;

}
