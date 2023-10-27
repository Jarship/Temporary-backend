package com.temporary.backend.rest.config;

import com.temporary.backend.exception.ErrorCode;
public class RestError {
    public final int code;
    public final String title;
    public final boolean success = false;
    public final String message;

    public RestError(ErrorCode errorCode, String title, String message) {
        this.code = errorCode.getErrorNumber();
        this.title = title;
        this.message = message;
    }

    public int getCode() { return code; }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public boolean getSuccess() { return success; }
}
