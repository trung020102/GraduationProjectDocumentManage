package com.doc.mamagement.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import vn.rananu.spring.mvc.exception.RestfulException;


@ResponseStatus(HttpStatus.FORBIDDEN)
public class NoPermissionException extends RestfulException {
    public NoPermissionException(String message) {
        super(HttpStatus.FORBIDDEN.value(), message);
    }
}
