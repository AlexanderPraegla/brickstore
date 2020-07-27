package edu.hm.brickstore.error;

import org.springframework.http.HttpStatus;

public class NotEnoughStockException extends BrickstoreException {

    public NotEnoughStockException() {
        super(ResponseCode.NOT_ENOUGH_STOCK, HttpStatus.BAD_REQUEST, "There is not enough stock of the requested inventory item");
    }


}
