package com.uijae.moments.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  // 파일 관련 에러
  FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생했습니다."),
  FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 중 오류가 발생했습니다."),
  FILE_IS_EMPTY(HttpStatus.BAD_REQUEST, "파일이 비어있습니다."),

  // 앨범 관련 에러
  ALBUM_NOT_FOUND(HttpStatus.NOT_FOUND, "앨범을 찾을 수 없습니다."),

  // 이미지 관련 에러
  IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다."),
  ;

  private final HttpStatus status;
  private final String detail;

  ErrorCode(HttpStatus status, String detail) {
    this.status = status;
    this.detail = detail;
  }
}
