package edu.hm.brickstore.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebitAccountDTO {
    @DecimalMin(value = "1")
    @Digits(integer = 7, fraction = 2)
    private BigDecimal debitAmount;
}
