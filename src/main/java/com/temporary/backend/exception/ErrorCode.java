package com.temporary.backend.exception;

public enum ErrorCode {
    UNKNOWN(99),
    NOT_FOUND(1),
    NOT_AUTHORIZED(2),
    BAD_INPUT(3),
    CONFLICT(4),
    DATABASE(20),
    INTEGRITY(21),
    ;

    private int errorNumber;
    ErrorCode(int number) {
        this.errorNumber = number;
    }

    public int getErrorNumber() {
        return errorNumber;
    }

    public static ErrorCode getErrorCodeByNumber(int number) {
        for (ErrorCode ec: ErrorCode.values())
            if (ec.getErrorNumber() == number) return ec;
        return null;
    }
}
