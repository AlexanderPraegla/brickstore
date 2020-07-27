package edu.hm.brickstore.order.dto;

import edu.hm.brickstore.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateDTO {
    @Min(1)
    private long orderId;
    @NotNull
    private OrderStatus status;
}
