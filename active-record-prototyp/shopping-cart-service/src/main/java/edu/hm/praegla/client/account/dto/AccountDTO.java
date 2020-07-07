package edu.hm.praegla.client.account.dto;

import lombok.Data;

@Data
public class AccountDTO {
    private long id;
    private CustomerDTO customer;
    private AddressDTO address;
    private double balance;
    private AccountStatus status;
}
