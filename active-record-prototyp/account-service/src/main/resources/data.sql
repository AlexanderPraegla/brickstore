--- account test data
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

--- shopping cart data
INSERT INTO public.address (id, city, postal_code, street)
VALUES (15, 'Freising', '85354', 'Bahnhofstraße 5a');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (15, 'Osker.Müller@test.com', 'Osker', 'Müller');
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (15, 100, 'ACTIVE', 15, 15);

--- order test data
INSERT INTO public.address (id, city, postal_code, street)
VALUES (16, 'Freising', '85354', 'Holzgartenstraße 5a');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (16, 'Moritz.Bauer@test.com', 'Moritz', 'Bauer');
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (16, 200, 'ACTIVE', 16, 16);

INSERT INTO public.address (id, city, postal_code, street)
VALUES (17, 'Freising', '85354', 'Holzgartenstraße 5a');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (17, 'Felix.Bauer@test.com', 'Felix', 'Bauer');
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (17, 50, 'ACTIVE', 17, 17);
--failCreateOrders

INSERT INTO public.address (id, city, postal_code, street)
VALUES (18, 'Freising', '85354', 'Holzgartenstraße 5a');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (18, 'Tobias.Bauer@test.com', 'Tobias', 'Bauer');
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (18, 500.00, 'ACTIVE', 18, 18);

INSERT INTO public.address (id, city, postal_code, street)
VALUES (19, 'Freising', '85354', 'Holzgartenstraße 5a');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (19, 'Peter.Bauer@test.com', 'Peter', 'Bauer');
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (19, 296.65, 'ACTIVE', 19, 19);

INSERT INTO public.address (id, city, postal_code, street)
VALUES (20, 'Freising', '85354', 'Holzgartenstraße 5a');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (20, 'Marion.Bauer@test.com', 'Marion', 'Bauer');
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (20, 400, 'ACTIVE', 20, 20);

INSERT INTO public.address (id, city, postal_code, street)
VALUES (21, 'Freising', '85354', 'Holzgartenstraße 5a');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (21, 'Erik.Bauer@test.com', 'Erik', 'Bauer');
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (21, 5, 'ACTIVE', 21, 21);

INSERT INTO public.address (id, city, postal_code, street)
VALUES (22, 'Freising', '85354', 'Holzgartenstraße 5a');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (22, 'Natalie.Bauer@test.com', 'Natalie', 'Bauer');
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (22, 1, 'INACTIVE', 22, 22);

INSERT INTO public.address (id, city, postal_code, street)
VALUES (23, 'Freising', '85354', 'Bürgermeisterstraße 7');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (23, 'Hans.Schmidt@test.com', 'Hans', 'Schmidt');
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (23, 50, 'ACTIVE', 23, 23);

--cancel orders
INSERT INTO public.address (id, city, postal_code, street)
VALUES (24, 'Freising', '85354', 'Bürgermeisterstraße 7');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (24, 'Erik.Schmidt@test.com', 'Erik', 'Schmidt');
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (24, 117.65, 'ACTIVE', 24, 24);

INSERT INTO public.address (id, city, postal_code, street)
VALUES (25, 'Freising', '85354', 'Bürgermeisterstraße 7');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (25, 'Birgit.Bauer@test.com', 'Birgit', 'Schmidt');
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (25, 26.02, 'ACTIVE', 25, 25);

INSERT INTO public.address (id, city, postal_code, street)
VALUES (26, 'Freising', '85354', 'Bürgermeisterstraße 7');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (26, 'Martin.Schmidt@test.com', 'Martin', 'Schmidt');
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (26, 20.01, 'ACTIVE', 26, 26);

ALTER SEQUENCE account_id_seq RESTART WITH 27;
ALTER SEQUENCE address_id_seq RESTART WITH 27;
ALTER SEQUENCE customer_id_seq RESTART WITH 27;
