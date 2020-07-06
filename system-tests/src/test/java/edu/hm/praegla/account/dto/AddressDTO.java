package edu.hm.praegla.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddressDTO {
    private long id;
    private String street;
    private String city;
    private String postalCode;
}
