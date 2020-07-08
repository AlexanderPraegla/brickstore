package edu.hm.praegla.order.entity;

public enum OrderStatus {
    CREATED,
    PAYED,
    PROCESSED,
    SHIPPED,
    DELIVERED,
    CANCELED,
    CANCELED_REFUNDED,
    CANCELED_RESTOCKED,
    CANCELLATION_COMPLETED
}
