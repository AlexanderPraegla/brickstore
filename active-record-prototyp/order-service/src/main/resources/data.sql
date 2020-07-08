--order test data
INSERT INTO public.shipping_address (id, customer_name, city, postal_code, street)
VALUES (1, 'Felix Bauer', 'Freising', '85354', 'Holzgartenstraße 5a');
INSERT INTO public.orders (id, account_id, total, status, created_on, modified_on, shipping_address_id)
VALUES (1, 17, 49.99, 'PROCESSED', '2020-07-07 17:01:18', '2020-07-07 17:01:18', 1);
INSERT INTO public.order_item (id, inventory_item_id, delivery_time, name, price, quantity, order_id)
VALUES (1, 21, 1, 'GoT - Die große Mauer', 49.99, 1, 1);

INSERT INTO public.shipping_address (id, customer_name, city, postal_code, street)
VALUES (2, 'Hans Schmidt', 'Freising', '85354', 'Bürgermeisterstraße 7');
INSERT INTO public.orders (id, account_id, total, status, created_on, modified_on, shipping_address_id)
VALUES (2, 23, 499.98, 'PAYED', '2020-07-06 17:01:18', '2020-07-06 17:01:18', 2);
INSERT INTO public.order_item (id, inventory_item_id, delivery_time, name, price, quantity, order_id)
VALUES (2, 21, 1, 'Bauwerke - Allianz Arena', 249.99, 2, 2);

--cancel orders
INSERT INTO public.shipping_address (id, customer_name, city, postal_code, street)
VALUES (3, 'Erik Schmidt', 'Freising', '85354', 'Bürgermeisterstraße 7');
INSERT INTO public.orders (id, account_id, total, status, created_on, modified_on, shipping_address_id)
VALUES (3, 24, 17.99, 'CREATED', '2020-07-06 17:01:18', '2020-07-06 17:01:18', 3);
INSERT INTO public.order_item (id, inventory_item_id, delivery_time, name, price, quantity, order_id)
VALUES (3, 27, 1, 'Bauwerke - Frauenkirche Dresden', 17.99, 1, 3);

INSERT INTO public.shipping_address (id, customer_name, city, postal_code, street)
VALUES (4, 'Birgit Schmidt', 'Freising', '85354', 'Bürgermeisterstraße 7');
INSERT INTO public.orders (id, account_id, total, status, created_on, modified_on, shipping_address_id)
VALUES (4, 25, 35.98, 'PAYED', '2020-07-06 17:01:18', '2020-07-06 17:01:18', 4);
INSERT INTO public.order_item (id, inventory_item_id, delivery_time, name, price, quantity, order_id)
VALUES (4, 27, 1, 'Bauwerke - Frauenkirche Dresden', 17.99, 2, 4);

INSERT INTO public.shipping_address (id, customer_name, city, postal_code, street)
VALUES (5, 'Martin Schmidt', 'Freising', '85354', 'Bürgermeisterstraße 7');
INSERT INTO public.orders (id, account_id, total, status, created_on, modified_on, shipping_address_id)
VALUES (5, 26, 53.97, 'PROCESSED', '2020-07-06 17:01:18', '2020-07-06 17:01:18', 5);
INSERT INTO public.order_item (id, inventory_item_id, delivery_time, name, price, quantity, order_id)
VALUES (5, 27, 1, 'Bauwerke - Frauenkirche Dresden', 17.99, 3, 5);

ALTER SEQUENCE orders_id_seq RESTART WITH 6;
ALTER SEQUENCE shipping_address_id_seq RESTART WITH 6;
ALTER SEQUENCE order_item_id_seq RESTART WITH 6;
