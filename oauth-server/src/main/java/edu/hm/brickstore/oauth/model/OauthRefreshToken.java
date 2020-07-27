package edu.hm.brickstore.oauth.model;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "oauth_refresh_token")
public class OauthRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "token_id")
    private String tokenId;

    @Column(name = "token")
    private byte[] token;

    @Column(name = "authentication")
    private byte[] authentication;
}

