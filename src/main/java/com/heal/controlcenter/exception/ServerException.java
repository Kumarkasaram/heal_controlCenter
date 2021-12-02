package com.heal.controlcenter.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class ServerException extends Exception {
    private final String errorMessage;

    public ServerException(Throwable cause, String errorMessage) {
        super(errorMessage, cause);
        this.errorMessage = errorMessage;
    }

    public ServerException(String errorMessage)
    {
        super("ServerException : "+ errorMessage);
        this.errorMessage  = errorMessage;
    }

    public String getSimpleMessage()    {
        return "ServerException :: "+this.errorMessage;
    }

}
