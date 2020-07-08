package edu.hm.praegla.client.account.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDTO {
    private long id;
    private CustomerDTO customer;
    private AddressDTO address;
    private BigDecimal balance;
    private AccountStatus status;
}
