package com.uijae.moments.album.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlbumResponseDto {
  private String title;
  private String type;
  private String summary;
  private LocalDate createdDate;

  // user, group 작업 후 정보 가져올 예정
//  private String userName;
// private String groupName;
}
