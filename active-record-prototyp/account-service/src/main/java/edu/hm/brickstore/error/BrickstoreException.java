package edu.hm.brickstore.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BrickstoreException extends RuntimeException {

    private final ResponseCode responseCode;
    private final HttpStatus httpStatus;

    BrickstoreException(ResponseCode responseCode, HttpStatus httpStatus, String message) {
        super(message);
        this.responseCode = responseCode;
        this.httpStatus = httpStatus;
    }
}
