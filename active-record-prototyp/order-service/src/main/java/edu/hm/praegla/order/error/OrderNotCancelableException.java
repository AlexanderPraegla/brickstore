package edu.hm.praegla.order.error;

import org.springframework.http.HttpStatus;

public class OrderNotCancelableException extends BrickstoreException {

    public OrderNotCancelableException() {
        super(ResponseCode.ORDER_NOT_CANCELABLE, HttpStatus.BAD_REQUEST, "The order is no longer cancelable");
    }


}
