package edu.hm.brickstore.oauth.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"userName"}, name = "USER_UNIQUE_USERNAME"))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 50)
    private String userName;

    @Column
    private String password;

    @Column
    private Boolean accountExpired;

    @Column
    private Boolean accountLocked;

    @Column
    private Boolean credentialsExpired;

    @Column
    private Boolean enabled;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "user", targetEntity = UserAuthority.class, cascade = {
            CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserAuthority> userAuthorities = new HashSet<>();

}
