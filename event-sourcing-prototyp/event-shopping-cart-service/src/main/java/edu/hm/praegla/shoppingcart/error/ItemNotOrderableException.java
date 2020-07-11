package edu.hm.praegla.shoppingcart.error;

import org.springframework.http.HttpStatus;

public class ItemNotOrderableException extends BrickstoreException {

    public ItemNotOrderableException() {
        super(ResponseCode.ITEM_NOT_ORDERABLE, HttpStatus.BAD_REQUEST, "This item is not orderable at the moment");
    }


}
