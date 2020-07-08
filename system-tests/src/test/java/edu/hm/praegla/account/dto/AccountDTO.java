package edu.hm.praegla.account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDTO {
    private long id;
    private CustomerDTO customer;
    private AddressDTO address;
    private BigDecimal balance;
    private String status;
}
