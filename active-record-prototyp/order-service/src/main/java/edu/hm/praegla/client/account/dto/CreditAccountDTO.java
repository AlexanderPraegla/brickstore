package edu.hm.praegla.client.account.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CreditAccountDTO {
    @DecimalMin(value = "1")
    @Column(precision = 7, scale = 2)
    private BigDecimal creditAmount;

    public CreditAccountDTO(@DecimalMin(value = "1") BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }
}
