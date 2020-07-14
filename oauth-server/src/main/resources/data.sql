insert into oauth_client_details (client_id, resource_ids, client_secret, scope,
                                  authorized_grant_types, web_server_redirect_uri, authorities,
                                  access_token_validity, refresh_token_validity, additional_information,
                                  autoapprove)
values ('brickstore-client-demo', 'demo-service,auth-server',
           /*clientSecretDemo*/'$2a$04$A5mUe11TcevOaU.2tRIvFOOXbpvvS/Zt2c8amvCpEl0f63rpi5IoK', 'demo',
        'password,authorization_code,refresh_token,client_credentials,implicit',
        'http://localhost:8080/oauth2/callback', 'admins', 10800, 2592000, null, null),
       ('brickstore-client-prototype',
        'account-service,inventory-service,order-service,shopping-cart-service,auth-server',
           /*clientSecretPrototype*/'$2a$04$pSHaonuSEFwv6UICtjx27.6FTTtyxhKwAQ9RKXnsQjfeCNI4RcK7a', 'brickstore',
        'password,authorization_code,refresh_token,client_credentials,implicit',
        'http://localhost:8080/oauth2/callback', 'admins', 10800, 2592000, null, null);

insert into authority (name)
values ('admins'),
       ('customers');

insert into users (account_expired, account_locked, credentials_expired, enabled, password,
                   user_name)
values (false, false, false, true, /*adminPassword*/'$2a$04$K7tMRFZr/wHrBLqfGpZycub5olS.MP3OEAEf.LjQ2zAqaSVk7tTV6',
        'admin'),
       (false, false, false, true, /*customerPassword*/'$2a$04$trCcQ9zpvGXxrK8vqHJYZuHS3.a/K34BLz/K.TowWMuuYhTyYVBm.',
        'customer');
insert into user_authority (authority_id, user_id)
values (1, 1),
       (2, 1),
       (2, 2);
