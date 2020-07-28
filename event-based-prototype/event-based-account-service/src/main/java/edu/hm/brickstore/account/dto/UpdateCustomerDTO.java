package edu.hm.brickstore.account.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateCustomerDTO {
    @NotNull
    private String firstname;
    @NotNull
    private String lastname;
    @NotNull
    private String email;
}
