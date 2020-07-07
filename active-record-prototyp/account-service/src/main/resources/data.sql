INSERT INTO public.address (id, city, postal_code, street)
VALUES (1, 'Rocky Beach', '97468', 'Am Schrottplatz 1');
INSERT INTO public.address (id, city, postal_code, street)
VALUES (2, 'Rocky Beach', '97468', 'Am Schrottplatz 2');
INSERT INTO public.address (id, city, postal_code, street)
VALUES (3, 'Am Stand 3', '97468', 'Rocky Beach');
INSERT INTO public.address (id, city, postal_code, street)
VALUES (4, 'Rocky Beach', '97468', 'Am Schrottplatz 3');
INSERT INTO public.address (id, city, postal_code, street)
VALUES (5, 'Rocky Beach', '97468', 'Am Polizeirevier 3');
INSERT INTO public.address (id, city, postal_code, street)
VALUES (6, 'Rocky Beach', '97468', 'Am Schrottplatz 4');
INSERT INTO public.address (id, city, postal_code, street)
VALUES (7, 'Rocky Beach', '97468', 'Am Schrottplatz 2');
INSERT INTO public.address (id, city, postal_code, street)
VALUES (8, 'Rocky Beach', '97468', 'Am Schrottplatz 3');
INSERT INTO public.address (id, city, postal_code, street)
VALUES (9, 'Santa Babara', '17468', 'Alfred Hitchcock Ave 19a');

INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (1, 'justus.jonas@dreifragezeichen.com', 'Justus', 'Jonas');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (2, 'peter.shaw@dreifragezeichen.com', 'Peter', 'Shaw');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (3, 'Kelly.Madigan@dreifragezeichen.com', 'Kelly', 'Madigan');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (4, 'Julius.Jonas@dreifragezeichen.com', 'Julius', 'Jonas');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (5, 'Samuel.Reynolds@dreifragezeichen.com', 'Samuel', 'Reynolds');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (6, 'Mathilda.Jonas@dreifragezeichen.com', 'Mathilda', 'Jonas');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (7, 'Titus.Jonas@dreifragezeichen.com', 'Titus', 'Jonas');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (8, 'Bob.Andrew@dreifragezeichen.com', 'Bob', 'Andrew');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (9, 'Alfred.Hitchcock@dreifragezeichen.com', 'Alfred', 'Hitchcock');

INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (1, 10.50, 'ACTIVE', 1, 1);
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (2, 0, 'CREATED', 2, 2);
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (3, 0, 'ACTIVE', 3, 3);
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (4, 0, 'ACTIVE', 4, 4);
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (5, 0, 'CREATED', 5, 5);
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (6, 10, 'CREATED', 6, 6);
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (7, 10, 'CREATED', 7, 7);
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (8, 0, 'ACTIVE', 8, 8);
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (9, 0, 'INACTIVE', 9, 9);

---shopping cart test data

INSERT INTO public.address (id, city, postal_code, street)
VALUES (10, 'Freising', '85354', 'Bahnhofstraße 3a');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (10, 'Martin.Maier@test.com', 'Martin', 'Maier');
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (10, 85, 'ACTIVE', 10, 10);

INSERT INTO public.address (id, city, postal_code, street)
VALUES (11, 'Freising', '85354', 'Bahnhofstraße 7a');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (11, 'Michael.Schmidt@test.com', 'Michael', 'Schmidt');
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (11, 12.98, 'ACTIVE', 11, 11);

INSERT INTO public.address (id, city, postal_code, street)
VALUES (12, 'Freising', '85354', 'Bahnhofstraße 5a');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (12, 'Peter.Müller@test.com', 'Peter', 'Müller');
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (12, 100, 'ACTIVE', 12, 12);

INSERT INTO public.address (id, city, postal_code, street)
VALUES (13, 'Freising', '85354', 'Bahnhofstraße 12a');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (13, 'Anja.Bauer@test.com', 'Anja', 'Bauer');
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (13, 100, 'INACTIVE', 13, 13);

INSERT INTO public.address (id, city, postal_code, street)
VALUES (14, 'Freising', '85354', 'Bahnhofstraße 5a');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (14, 'Claudia.Müller@test.com', 'Claudia', 'Müller');
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (14, 100, 'ACTIVE', 14, 14);

ALTER SEQUENCE account_id_seq RESTART WITH 10;
ALTER SEQUENCE address_id_seq RESTART WITH 10;
ALTER SEQUENCE customer_id_seq RESTART WITH 10;
