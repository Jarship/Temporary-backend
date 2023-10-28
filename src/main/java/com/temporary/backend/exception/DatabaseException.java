package com.temporary.backend.exception;

public class DatabaseException extends ApplicationException {
    public DatabaseException() {
        super("Database Exception");
    }

    public DatabaseException(Throwable t) {
        super(t.getMessage());
    }
}
