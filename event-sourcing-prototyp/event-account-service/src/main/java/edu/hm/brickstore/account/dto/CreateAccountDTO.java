package edu.hm.brickstore.account.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateAccountDTO {

    private long id;
    @NotNull
    private CustomerDTO customer;
    @NotNull
    private AddressDTO address;

    @Data
    public static class AddressDTO {

        @NotNull
        private String street;
        @NotNull
        private String city;
        @NotNull
        private String postalCode;
    }

    @Data
    public static class CustomerDTO {

        @NotNull
        private String firstname;
        @NotNull
        private String lastname;
        @NotNull
        private String email;

    }
}
