package com.uijae.moments.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  // 파일 관련 에러
  FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생했습니다."),
  FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 중 오류가 발생했습니다."),
  FILE_IS_EMPTY(HttpStatus.BAD_REQUEST, "파일이 비어있습니다."),
  FILE_COUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "업로드 가능한 파일 개수를 초과했습니다. (최대 10개)"),
  FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "파일 크기가 허용된 최대 크기를 초과했습니다. (최대 10MB)"),

  // 앨범 관련 에러
  ALBUM_NOT_FOUND(HttpStatus.NOT_FOUND, "앨범을 찾을 수 없습니다."),

  // 이미지 관련 에러
  IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다."),

  // 게시글 관련 에러
  POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
  INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "미래 날짜는 선택할 수 없습니다."),
  INVALID_WEEK_SELECTION(HttpStatus.BAD_REQUEST, "1주 또는 2주만 선택할 수 있습니다."),
  ;

  private final HttpStatus status;
  private final String detail;

  ErrorCode(HttpStatus status, String detail) {
    this.status = status;
    this.detail = detail;
  }
}
