package edu.hm.praegla.client.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ModifyAccountBalanceDTO {
    public BigDecimal amount;
}
