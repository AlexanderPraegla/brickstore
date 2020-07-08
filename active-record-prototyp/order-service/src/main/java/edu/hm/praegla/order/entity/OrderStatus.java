package edu.hm.praegla.order.entity;

public enum OrderStatus {
    CREATED,
    PAYED,
    PROCESSED,
    SHIPPED,
    DELIVERED,
    CANCELED,
    CANCELED_AMOUNT_REFUNDED,
    CANCELED_STOCK_RESTORED,
    CANCELLATION_COMPLETED
}
