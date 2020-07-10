package edu.hm.praegla.account.dto;

import edu.hm.praegla.account.entity.Account;
import lombok.Data;

@Data
public class CreateAccountDTO {
    private Account.Customer customer;
    private Account.Address address;
}
