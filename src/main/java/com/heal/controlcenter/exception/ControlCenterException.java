package com.heal.controlcenter.exception;

public class ControlCenterException extends Exception {
    private final String errorMessage;
    private Object errorObject;

    public ControlCenterException(String message, Throwable cause, String errorMessage) {
        super(message, cause);
        this.errorMessage = errorMessage;
    }

    public ControlCenterException(Object message, String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorObject = message;
    }

    public ControlCenterException(Throwable cause, String errorMessage) {
        super(cause);
        this.errorMessage = errorMessage;
    }

    public ControlCenterException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public String getSimpleMessage() {
        return "ControlCenterException :: " + this.errorMessage;
    }

    public Object getErrorObject() {
        return this.errorObject;
    }
}