package com.uijae.moments.album.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AlbumCreateRequestDto {
// 사용자, 그룹 부분 작업 후 추가
//  @NotNull(message = "사용자 ID는 필수 입력 항목입니다.")
//  private Long userId;
//
//  @NotNull(message = "그룹 ID는 필수 입력 항목입니다.")
//  private Long groupId;

  @NotBlank(message = "앨범 제목은 필수 입력 항목입니다.")
  @Size(max = 50, message = "최대 50자까지 입력 가능합니다.")
  private String title;

  @Size(max = 10, message = "최대 10자까지 입력 가능합니다.")
  private String type;

  @NotBlank(message = "앨범 요약은 필수 입력 항목입니다.")
  @Size(max = 100, message = "최대 100자까지 입력 가능합니다.")
  private String summary;
}
