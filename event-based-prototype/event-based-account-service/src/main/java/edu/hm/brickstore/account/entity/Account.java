package edu.hm.brickstore.account.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;

@Data
@Document
public class Account {

    @Transient
    public static final String SEQUENCE_NAME = "account_sequence";

    @Id
    private long id;
    private Customer customer;
    private Address address;
    @Digits(integer = 7, fraction = 2)
    private BigDecimal balance;

    private AccountStatus status;

    @Data
    public static class Address {

        private String street;
        private String city;
        private String postalCode;
    }

    @Data
    public static class Customer {

        private String firstname;
        private String lastname;
        private String email;

    }
}
