package com.qunar.cm.ic.common.exception;

/**
 * Created by dandan.sha on 2018/08/24.
 */
public class ICException extends RuntimeException {


    public ICException(String message) {
        super(message);
    }

    public ICException(ExceptionEnum exceptionEnum) {

        super(exceptionEnum.getMessage());
    }

    public ICException(ExceptionEnum exceptionEnum, String data) {

        super("[" + exceptionEnum.getMessage() + "]" + data);
    }

    public ICException(ExceptionEnum exceptionEnum, Throwable cause) {

        super(exceptionEnum.getMessage(), cause);
    }

    public ICException(String message, Throwable cause) {

        super(message, cause);
    }

    public ICException(ExceptionEnum exceptionEnum, String message, Throwable cause) {

        super("[" + exceptionEnum.getMessage() + "]" + message, cause);
    }

}
