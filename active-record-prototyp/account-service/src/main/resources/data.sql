INSERT INTO public.address (id, city, postal_code, street)
VALUES (1, 'Rocky Beach', '97468', 'Am Schrottplatz 1');
INSERT INTO public.address (id, city, postal_code, street)
VALUES (2, 'Rocky Beach', '97468', 'Am Schrottplatz 2');

INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (1, 'justus.jonas@dreifragezeichen.com', 'Justus', 'Jonas');
INSERT INTO public.customer (id, email, firstname, lastname)
VALUES (2, 'peter.shaw@dreifragezeichen.com', 'Peter', 'Shaw');

INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (1, 10.50, 'ACTIVE', 1, 1);
INSERT INTO public.account (id, balance, status, customer_id, address_id)
VALUES (2, 0, 'CREATED', 2, 2);

ALTER SEQUENCE account_id_seq RESTART WITH 3;
ALTER SEQUENCE address_id_seq RESTART WITH 3;
ALTER SEQUENCE customer_id_seq RESTART WITH 3;
