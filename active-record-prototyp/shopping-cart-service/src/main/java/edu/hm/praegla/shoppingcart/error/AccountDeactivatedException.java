package edu.hm.praegla.shoppingcart.error;

import org.springframework.http.HttpStatus;

public class AccountDeactivatedException extends BrickstoreException {

    public AccountDeactivatedException() {
        super(ResponseCode.ACCOUNT_DEACTIVATED, HttpStatus.BAD_REQUEST, "Could not add item to shopping cart of deactivated account");
    }
}
