package com.uijae.moments.post.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponseDto {

  private String title;
  private LocalDateTime createdDateTime;

//  private String userName;
}
