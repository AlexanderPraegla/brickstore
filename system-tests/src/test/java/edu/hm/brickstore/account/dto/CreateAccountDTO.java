package edu.hm.brickstore.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAccountDTO {
    private CustomerDTO customer;
    private AddressDTO address;
}
