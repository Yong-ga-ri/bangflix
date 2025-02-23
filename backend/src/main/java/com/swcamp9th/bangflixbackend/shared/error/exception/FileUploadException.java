package com.swcamp9th.bangflixbackend.shared.error.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;

public class FileUploadException extends BusinessException {
    public FileUploadException() {
        super(ErrorCode.FILE_UPLOAD_ERROR);
    }
}
