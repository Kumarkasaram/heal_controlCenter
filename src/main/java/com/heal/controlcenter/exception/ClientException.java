package com.heal.controlcenter.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class ClientException extends Exception {
    private final String errorMessage;

    public ClientException(Throwable cause, String errorMessage) {
        super(errorMessage, cause);
        this.errorMessage = errorMessage;
    }

    public ClientException(String errorMessage)
    {
        super("ClientException : "+ errorMessage);
        this.errorMessage  = errorMessage;
    }

    public String getSimpleMessage()    {
        return "ClientException :: "+this.errorMessage;
    }

}
