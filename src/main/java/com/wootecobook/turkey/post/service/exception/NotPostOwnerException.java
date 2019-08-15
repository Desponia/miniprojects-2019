package com.wootecobook.turkey.post.service.exception;

public class NotPostOwnerException extends RuntimeException {

    private static final String NOT_POST_OWENER_ERROR_MESSAGE = "삭제 권한이 없는 사용자 입니다.";

    public NotPostOwnerException() {
        super(NOT_POST_OWENER_ERROR_MESSAGE);
    }
}
