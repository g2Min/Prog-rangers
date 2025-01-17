package com.prograngers.backend.exception.notfound;

import static com.prograngers.backend.exception.ErrorCode.COMMENT_ALREADY_DELETED;
import static com.prograngers.backend.exception.ErrorCode.COMMENT_NOT_FOUND;

public class CommentAlreadyDeletedException extends NotFoundException {
    public CommentAlreadyDeletedException() {
        super(COMMENT_ALREADY_DELETED,"이미 삭제된 댓글입니다");
    }
}
