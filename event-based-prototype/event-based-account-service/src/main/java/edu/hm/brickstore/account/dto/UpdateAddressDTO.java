package edu.hm.brickstore.account.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateAddressDTO {
    @NotNull
    private String street;
    @NotNull
    private String city;
    @NotNull
    private String postalCode;
}
