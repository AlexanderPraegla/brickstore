package edu.hm.brickstore.client.error;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import edu.hm.brickstore.error.ResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FeignBadRequestException extends HystrixBadRequestException {
    private final ResponseCode responseCode;
    private final HttpStatus httpStatus;

    public FeignBadRequestException(String message, ResponseCode responseCode, HttpStatus httpStatus) {
        super(message);
        this.responseCode = responseCode;
        this.httpStatus = httpStatus;
    }
}
