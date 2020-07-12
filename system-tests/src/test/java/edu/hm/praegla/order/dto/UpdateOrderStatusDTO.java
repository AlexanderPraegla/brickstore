package edu.hm.praegla.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateOrderStatusDTO {
    private long orderId;
    private String status;
}
