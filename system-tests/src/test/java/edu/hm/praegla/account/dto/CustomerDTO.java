package edu.hm.praegla.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerDTO {

    private long id;
    private String firstname;
    private String lastname;
    private String email;
}
