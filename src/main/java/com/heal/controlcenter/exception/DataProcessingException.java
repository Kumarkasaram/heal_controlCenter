package com.heal.controlcenter.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class DataProcessingException extends Exception {
    private final String errorMessage;

    public DataProcessingException(Throwable cause, String errorMessage) {
        super(errorMessage, cause);
        this.errorMessage = errorMessage;
    }

    public DataProcessingException(String errorMessage)
    {
        super("DataProcessingException : "+ errorMessage);
        this.errorMessage  = errorMessage;
    }

    public String getSimpleMessage()    {
        return "DataProcessingException :: "+this.errorMessage;
    }

}