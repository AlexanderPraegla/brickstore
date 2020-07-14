package edu.hm.praegla.error;

import org.springframework.http.HttpStatus;

public class ItemNotOrderableException extends BrickstoreException {

    public ItemNotOrderableException() {
        super(ResponseCode.ITEM_NOT_ORDERABLE, HttpStatus.BAD_REQUEST, "This item is not orderable at the moment");
    }


}
