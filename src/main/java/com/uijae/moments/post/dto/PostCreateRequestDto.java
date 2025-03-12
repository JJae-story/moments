package com.uijae.moments.post.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class PostCreateRequestDto {

  @NotBlank(message = "제목은 필수 입력 값입니다.")
  private String title;

  private String content;

  private List<MultipartFile> files;
}
