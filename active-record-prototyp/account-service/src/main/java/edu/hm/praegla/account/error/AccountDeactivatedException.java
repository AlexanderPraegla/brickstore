package edu.hm.praegla.account.error;

import org.springframework.http.HttpStatus;

public class AccountDeactivatedException extends BrickstoreException {

    public AccountDeactivatedException() {
        super(ResponseCode.ACCOUNT_DEACTIVATED, HttpStatus.BAD_REQUEST, "This account is deactivated");
    }
}
