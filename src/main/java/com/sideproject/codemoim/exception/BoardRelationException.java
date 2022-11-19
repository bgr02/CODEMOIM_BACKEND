package com.sideproject.codemoim.exception;

public class BoardRelationException extends RuntimeException {

    public BoardRelationException(String message) {
        super(message);
    }

    public BoardRelationException(String message, Throwable cause) {
        super(message, cause);
    }

}
