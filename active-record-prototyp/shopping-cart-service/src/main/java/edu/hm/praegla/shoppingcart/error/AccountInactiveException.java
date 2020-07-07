package edu.hm.praegla.shoppingcart.error;

import org.springframework.http.HttpStatus;

public class AccountInactiveException extends BrickstoreException {

    public AccountInactiveException() {
        super(ResponseCode.ACCOUNT_INACTIVE, HttpStatus.BAD_REQUEST, "Could not add item to shopping cart of inactive user");
    }
}
