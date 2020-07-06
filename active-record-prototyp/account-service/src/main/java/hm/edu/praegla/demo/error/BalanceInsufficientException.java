package hm.edu.praegla.demo.error;

import org.springframework.http.HttpStatus;

public class BalanceInsufficientException extends BrickstoreException {

    public BalanceInsufficientException() {
        super(ResponseCode.BALANCE_INSUFFICIENT, HttpStatus.BAD_REQUEST, "The balance is not sufficient");
    }


}
