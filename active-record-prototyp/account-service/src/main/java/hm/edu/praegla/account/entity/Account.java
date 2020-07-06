package hm.edu.praegla.account.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private Customer customer;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    private double balance;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private AccountStatus status;
}
