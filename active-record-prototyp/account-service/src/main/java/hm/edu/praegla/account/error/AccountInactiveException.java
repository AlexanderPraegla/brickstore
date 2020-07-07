package hm.edu.praegla.account.error;

import org.springframework.http.HttpStatus;

public class AccountInactiveException extends BrickstoreException {

    public AccountInactiveException() {
        super(ResponseCode.ACCOUNT_INACTIVE, HttpStatus.BAD_REQUEST, "This account is inactive");
    }
}
