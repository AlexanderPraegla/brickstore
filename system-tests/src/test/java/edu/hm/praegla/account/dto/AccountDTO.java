package edu.hm.praegla.account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDTO {
    private long id;
    private CustomerDTO customer;
    private AddressDTO address;
    private double balance;
    private String status;
}
