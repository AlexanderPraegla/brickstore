package edu.hm.brickstore.client.account.dto;

import lombok.Data;

@Data
public class CustomerDTO {

    private long id;
    private String firstname;
    private String lastname;
    private String email;
}
