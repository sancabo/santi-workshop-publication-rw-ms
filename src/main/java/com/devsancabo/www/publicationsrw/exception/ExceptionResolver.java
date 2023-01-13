package com.devsancabo.www.publicationsrw.exception;

import com.devsancabo.www.publicationsrw.dto.ErrorDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class ExceptionResolver {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> handleValidationException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ErrorDTO((e.getLocalizedMessage())));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDTO> handleRuntimeException(MissingServletRequestParameterException e) {
        return ResponseEntity.internalServerError().body(new ErrorDTO((e.getLocalizedMessage())));
    }
}
