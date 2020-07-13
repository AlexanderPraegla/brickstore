package edu.hm.praegla.order.entity;

public enum OrderStatus {
    CREATED,
    PAYED,
    PROCESSED,
    SHIPPED,
    DELIVERED,
    CANCELED,
    CANCELED_AMOUNT_REFUNDED,
    CANCELLATION_COMPLETED;

    public boolean isOneOf(OrderStatus... orderStatus) {
        for (OrderStatus status : orderStatus) {
            if (this == status) {
                return true;
            }
        }
        return false;
    }
}
