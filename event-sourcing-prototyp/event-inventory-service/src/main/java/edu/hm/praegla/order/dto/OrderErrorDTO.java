package edu.hm.praegla.order.dto;

import edu.hm.praegla.error.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderErrorDTO {
    @Min(1)
    private long orderId;
    @NotNull
    private ResponseCode errorCode;
}
