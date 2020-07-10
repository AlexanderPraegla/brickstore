package edu.hm.praegla.account.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

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

    @Column(precision = 7, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private AccountStatus status;
}
