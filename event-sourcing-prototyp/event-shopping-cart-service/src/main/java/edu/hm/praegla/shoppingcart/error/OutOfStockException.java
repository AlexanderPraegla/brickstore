package edu.hm.praegla.shoppingcart.error;

import org.springframework.http.HttpStatus;

public class OutOfStockException extends BrickstoreException {

    public OutOfStockException() {
        super(ResponseCode.OUT_OF_STOCK, HttpStatus.BAD_REQUEST, "The inventory item is no longer available");
    }


}
