package com.doc.mamagement.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import vn.rananu.spring.mvc.exception.RestfulException;


@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class UnprocessableException extends RestfulException {
    public UnprocessableException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY.value(), message);
    }
}
