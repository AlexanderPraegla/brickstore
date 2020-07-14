package edu.hm.praegla.error;

import edu.hm.praegla.order.entity.OrderStatus;
import org.springframework.http.HttpStatus;

public class InvalidOrderStatusChangeException extends BrickstoreException {

    public InvalidOrderStatusChangeException(OrderStatus from, OrderStatus to) {
        super(ResponseCode.INVALID_ORDER_STATUS_CHANGE, HttpStatus.BAD_REQUEST, generateMessage(from, to));
    }

    private static String generateMessage(OrderStatus from, OrderStatus to) {
        return String.format("The order status change from '%s' to '%s' is not allowed", from, to);
    }


}
