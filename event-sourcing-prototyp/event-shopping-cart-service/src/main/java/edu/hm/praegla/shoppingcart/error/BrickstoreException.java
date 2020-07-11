package edu.hm.praegla.shoppingcart.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BrickstoreException extends RuntimeException {

    private ResponseCode responseCode;
    private HttpStatus httpStatus;

    BrickstoreException(ResponseCode responseCode, HttpStatus httpStatus, String message) {
        super(message);
        this.responseCode = responseCode;
        this.httpStatus = httpStatus;
    }

    public BrickstoreException(Throwable throwable) {
        super(throwable);
    }
}
