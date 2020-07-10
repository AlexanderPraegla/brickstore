package edu.hm.praegla.account.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

@Data
public class CreditAccountDTO {
    @DecimalMin(value = "1")
    @Digits(integer = 7, fraction = 2)
    private BigDecimal creditAmount;
}
