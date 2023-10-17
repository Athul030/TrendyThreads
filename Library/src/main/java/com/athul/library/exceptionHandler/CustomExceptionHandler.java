package com.athul.library.exceptionHandler;

import com.athul.library.exception.CustomLazyInitializationException;
import com.athul.library.exception.OutOfStockException;
import com.athul.library.exception.TwilioVerificationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(TwilioVerificationException.class)
    public ResponseEntity<Object> TwilioVerificationException(TwilioVerificationException e){

        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CustomLazyInitializationException.class)
    public ResponseEntity<Object> handleCustomLazyInitializationException(CustomLazyInitializationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }




}
