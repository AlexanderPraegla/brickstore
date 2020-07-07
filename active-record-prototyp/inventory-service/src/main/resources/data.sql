INSERT INTO public.inventory_item (id, delivery_time, name, price, status, stock)
VALUES (1, 2, 'Harry Potter - Große Halle', 299.99, 'AVAILABLE', 5);
INSERT INTO public.inventory_item (id, delivery_time, name, price, status, stock)
VALUES (2, 1, 'Harry Potter - Hagrids Hütte', 49.99, 'AVAILABLE', 10);
INSERT INTO public.inventory_item (id, delivery_time, name, price, status, stock)
VALUES (3, 1, 'Harry Potter - Peitschende Weide', 59.99, 'OUT_OF_STOCK', 0);
INSERT INTO public.inventory_item (id, delivery_time, name, price, status, stock)
VALUES (4, 7, 'Harry Potter - Fuchsbau', 99.99, 'AVAILABLE', 3);
INSERT INTO public.inventory_item (id, delivery_time, name, price, status, stock)
VALUES (5, 7, 'Harry Potter - Durmstrangs Schiff' , 199.99, 'DEACTIVATED', 3);

INSERT INTO public.inventory_item (id, delivery_time, name, price, status, stock)
VALUES (6, 2, 'Herr der Ringe - Helms Klamm', 149.99, 'AVAILABLE', 10);
INSERT INTO public.inventory_item (id, delivery_time, name, price, status, stock)
VALUES (7, 7, 'Herr der Ringe - Minas Tirith', 629.99, 'AVAILABLE', 1);
INSERT INTO public.inventory_item (id, delivery_time, name, price, status, stock)
VALUES (8, 3, 'Herr der Ringe - Isengard', 129.99, 'OUT_OF_STOCK', 0);
INSERT INTO public.inventory_item (id, delivery_time, name, price, status, stock)
VALUES (9, 6, 'Herr der Ringe - Beutelsend', 49.99, 'AVAILABLE', 2);
INSERT INTO public.inventory_item (id, delivery_time, name, price, status, stock)
VALUES (10, 6, 'Herr der Ringe - Elbenschiff', 89.99, 'AVAILABLE', 1);

---shopping cart test data
INSERT INTO public.inventory_item (id, delivery_time, name, price, status, stock)
VALUES (11, 1, 'Star Wars - Millennium Falke', 699.99, 'AVAILABLE', 3);
INSERT INTO public.inventory_item (id, delivery_time, name, price, status, stock)
VALUES (12, 4, 'Star Wars - TIE Fighter', 99.99, 'AVAILABLE', 1);
INSERT INTO public.inventory_item (id, delivery_time, name, price, status, stock)
VALUES (13, 2, 'Star Wars - X-Wing', 199.99, 'AVAILABLE', 10);
INSERT INTO public.inventory_item (id, delivery_time, name, price, status, stock)
VALUES (13, 7, 'Star Wars - X-Wing', 199.99, 'AVAILABLE', 10);
INSERT INTO public.inventory_item (id, delivery_time, name, price, status, stock)
VALUES (14, 9, 'Star Wars - A-Wing', 29.99, 'AVAILABLE', 1);
INSERT INTO public.inventory_item (id, delivery_time, name, price, status, stock)
VALUES (15, 2, 'Star Wars - TIE Bomber', 19.99, 'OUT_OF_STOCK', 0);
INSERT INTO public.inventory_item (id, delivery_time, name, price, status, stock)
VALUES (16, 1, 'Star Wars - Sternenzerstörer', 799.99, 'AVAILABLE', 1);

ALTER SEQUENCE inventory_item_id_seq RESTART WITH 11;
