package edu.hm.praegla.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShippingAddressDTO {

    private String customerName;
    private String street;
    private String city;
    private String postalCode;
}
