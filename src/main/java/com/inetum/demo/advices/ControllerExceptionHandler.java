package com.inetum.demo.advices;

import com.inetum.demo.dtos.ErrorMessage;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.util.Date;
@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<ErrorMessage>
    resourceNotFoundException(ResourceNotFoundException ex, WebRequest
            request) {
        ErrorMessage message = new ErrorMessage(
                404,
                new Date(),
                ex.getMessage(),
                "Error capturado por ResourceNotFoundException");
        return new ResponseEntity<ErrorMessage>(message,
                HttpStatus.NOT_FOUND);
    }
}
