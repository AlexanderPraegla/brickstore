package edu.hm.brickstore.order.entity;

public enum OrderStatus {
    CREATED,
    PAYED,
    PROCESSED,
    SHIPPED,
    DELIVERED,
    CANCELED,
    CANCELED_AMOUNT_REFUNDED,
    CANCELLATION_COMPLETED
}
