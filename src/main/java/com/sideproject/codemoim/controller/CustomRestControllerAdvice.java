package com.sideproject.codemoim.controller;

import com.sideproject.codemoim.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.mail.MessagingException;
import javax.management.relation.RelationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Slf4j
@RestControllerAdvice
public class CustomRestControllerAdvice {

    @ExceptionHandler(BoardNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public void boardNotFoundException(BoardNotFoundException exception) {
        exceptionMessagePrintFormatter(exception, "BoardNotFoundException");
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public void profileNotFoundException(ProfileNotFoundException exception) {
        exceptionMessagePrintFormatter(exception, "ProfileNotFoundException");
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public void postNotFoundException(PostNotFoundException exception) {
        exceptionMessagePrintFormatter(exception, "PostNotFoundException");
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public void commentNotFoundException(CommentNotFoundException exception) {
        exceptionMessagePrintFormatter(exception, "CommentNotFoundException");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public void usernameNotFoundException(UsernameNotFoundException exception) {
        exceptionMessagePrintFormatter(exception, "UsernameNotFoundException");
    }

    @ExceptionHandler(TagNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public void tagNotFoundException(TagNotFoundException exception) {
        exceptionMessagePrintFormatter(exception, "TagNotFoundException");
    }

    @ExceptionHandler(InvalidSecretKeyException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void invalidSecretKeyException(InvalidSecretKeyException exception) {
        exceptionMessagePrintFormatter(exception, "InvalidSecretKeyException");
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void badRequestException(BadRequestException exception) {
        exceptionMessagePrintFormatter(exception, "BadRequestException");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void illegalArgumentException(IllegalArgumentException exception) {
        exceptionMessagePrintFormatter(exception, "IllegalArgumentException");
    }

    @ExceptionHandler(RelationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void relationException(RelationException exception) {
        exceptionMessagePrintFormatter(exception, "RelationException");
    }

    @ExceptionHandler(DuplicateTagException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void duplicateTagException(DuplicateTagException exception) {
        exceptionMessagePrintFormatter(exception, "DuplicateTagException");
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void duplicateUsernameException(DuplicateUsernameException exception) {
        exceptionMessagePrintFormatter(exception, "DuplicateUsernameException");
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void duplicateEmailException(DuplicateEmailException exception) {
        exceptionMessagePrintFormatter(exception, "DuplicateEmailException");
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void passwordNotMatchException(PasswordNotMatchException exception) {
        exceptionMessagePrintFormatter(exception, "PasswordNotMatchException");
    }

    @ExceptionHandler(ReissueAccessTokenErrorException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void reissueAccessTokenErrorException(ReissueAccessTokenErrorException exception) {
        exceptionMessagePrintFormatter(exception, "ReissueAccessTokenErrorException");
    }

    @ExceptionHandler(MessagingException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public void messagingException(MessagingException exception) {
        exceptionMessagePrintFormatter(exception, "MessagingException");
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public void inputOutputException(IOException exception) {
        exceptionMessagePrintFormatter(exception, "IOException");
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public void nullPointerException(NullPointerException exception) {
        exceptionMessagePrintFormatter(exception, "NullPointerException");
    }

    @ExceptionHandler(UnsupportedEncodingException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void unsupportedEncodingException(UnsupportedEncodingException exception) {
        exceptionMessagePrintFormatter(exception, "UnsupportedEncodingException");
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public void objectOptimisticLockingFailureException(ObjectOptimisticLockingFailureException exception) {
        exceptionMessagePrintFormatter(exception, "ObjectOptimisticLockingFailureException");
    }

    @ExceptionHandler(TagRelationException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public void tagRelationException(TagRelationException exception) {
        exceptionMessagePrintFormatter(exception, "TagRelationException");
    }

    @ExceptionHandler(BoardRelationException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public void boardRelationException(BoardRelationException exception) {
        exceptionMessagePrintFormatter(exception, "BoardRelationException");
    }

    private void exceptionMessagePrintFormatter(Exception exception, String errorName) {
        StackTraceElement[] ste = exception.getStackTrace();

        String className = ste[0].getClassName();
        String methodName = ste[0].getMethodName();
        int lineNumber = ste[0].getLineNumber();
        String fileName = ste[0].getFileName();

        log.error(errorName + ": " + exception.getMessage());
        log.error("[Class] => " + className + ", [Method] => " + methodName + ", [File] => " + fileName + ", [Line] => " + lineNumber);
    }

}
