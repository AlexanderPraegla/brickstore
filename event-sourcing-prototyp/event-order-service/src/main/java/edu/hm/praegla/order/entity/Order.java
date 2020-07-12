package edu.hm.praegla.order.entity;

import edu.hm.praegla.order.error.ResponseCode;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@Document(collection = "order")
public class Order {

    @Transient
    public static final String SEQUENCE_NAME = "order-sequence";

    @Id
    private long id;
    @Min(1)
    private long accountId;
    @Digits(integer = 7, fraction = 2)
    private BigDecimal total;
    @NotNull
    private ShippingAddress shippingAddress;
    @NotNull
    private List<OrderItem> orderItems;
    private OrderStatus status;
    private ResponseCode errorCode;
}
