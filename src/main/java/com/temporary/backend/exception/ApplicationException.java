package com.temporary.backend.exception;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import java.net.HttpURLConnection;

public class ApplicationException extends ServletException {
    private static final String NOT_FOUND_ERROR_TITLE = "Not Found";
    private static final String UNAUTHORIZED_TITLE = "Unauthorized Access";

    protected int httpStatusCode;
    protected ErrorCode errorCode;

    public ApplicationException(Throwable rootCause, String message, int statusCode, ErrorCode errorCode) {
        super(message, rootCause);
        this.httpStatusCode = statusCode;
        this.errorCode = errorCode;
    }

    public ApplicationException(String message, int statusCode, ErrorCode errorCode) {
        this(null, message, statusCode, errorCode);
    }

    public ApplicationException(String message) {
        this(message, HttpURLConnection.HTTP_INTERNAL_ERROR, ErrorCode.UNKNOWN);
    }

    public ApplicationException(Throwable rootCause) {
        this(rootCause.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR, ErrorCode.UNKNOWN);
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public static class UnauthorizedException extends ApplicationException {
        public UnauthorizedException() {
            super(UNAUTHORIZED_TITLE, HttpURLConnection.HTTP_UNAUTHORIZED, ErrorCode.NOT_AUTHORIZED);
        }

        public UnauthorizedException(String message) {
            super(message, HttpsURLConnection.HTTP_UNAUTHORIZED, ErrorCode.NOT_AUTHORIZED);
        }
    }

    public static class NotFoundException extends ApplicationException {
        public NotFoundException() {
            super(NOT_FOUND_ERROR_TITLE, HttpsURLConnection.HTTP_NOT_FOUND, ErrorCode.NOT_FOUND);
        }
        public NotFoundException(String message) {
            super(message, HttpURLConnection.HTTP_NOT_FOUND, ErrorCode.NOT_FOUND);
        }
    }

    public static class EmailAlreadyRegisteredException extends ApplicationException {
        public EmailAlreadyRegisteredException() {
            super("Email already registered", HttpURLConnection.HTTP_BAD_REQUEST, ErrorCode.BAD_INPUT);
        }
    }

    public static class ValidationException extends ApplicationException {
        public ValidationException() {
            super("Validation failed.", HttpURLConnection.HTTP_BAD_REQUEST, ErrorCode.BAD_INPUT);
        }

        public ValidationException(String message) {
            super(message, HttpURLConnection.HTTP_BAD_REQUEST, ErrorCode.BAD_INPUT);
        }
    }
}
