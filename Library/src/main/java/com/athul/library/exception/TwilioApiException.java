package com.athul.library.exception;

public class TwilioApiException extends RuntimeException{

    public TwilioApiException(String message) {
        super(message);
    }
}
