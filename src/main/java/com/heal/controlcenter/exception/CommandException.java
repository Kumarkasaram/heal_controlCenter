package com.heal.controlcenter.exception;

public class CommandException extends Exception {

    public CommandException(String errorMessage) {
        super("CommandException : "+ errorMessage);
    }

    public CommandException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
