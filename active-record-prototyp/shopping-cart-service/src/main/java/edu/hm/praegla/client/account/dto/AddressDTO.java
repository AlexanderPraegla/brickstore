package edu.hm.praegla.client.account.dto;


import lombok.Data;

@Data
public class AddressDTO {
    private long id;
    private String street;
    private String city;
    private String postalCode;
}
