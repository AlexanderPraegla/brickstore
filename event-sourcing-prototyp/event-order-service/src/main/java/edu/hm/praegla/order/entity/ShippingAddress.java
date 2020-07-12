package edu.hm.praegla.order.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ShippingAddress {

    @NotNull
    private String customerName;
    @NotNull
    private String city;
    @NotNull
    private String street;
    @NotNull
    private String postalCode;

}

