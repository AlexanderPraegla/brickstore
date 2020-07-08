INSERT INTO public.shopping_cart (id, account_id)
VALUES (1, 14);
INSERT INTO public.shopping_cart (id, account_id)
VALUES (2, 15);

INSERT INTO public.line_item (id, inventory_item_id, quantity, shopping_cart_id)
VALUES (1, 17, 1, 1);
INSERT INTO public.line_item (id, inventory_item_id, quantity, shopping_cart_id)
VALUES (2, 18, 2, 1);
INSERT INTO public.line_item (id, inventory_item_id, quantity, shopping_cart_id)
VALUES (3, 17, 1, 2);

---order test data

INSERT INTO public.shopping_cart (id, account_id)
VALUES (3, 16);
INSERT INTO public.line_item (id, inventory_item_id, quantity, shopping_cart_id)
VALUES (4, 19, 2, 3);
INSERT INTO public.line_item (id, inventory_item_id, quantity, shopping_cart_id)
VALUES (5, 20, 3, 3);

--
INSERT INTO public.shopping_cart (id, account_id)
VALUES (4, 17);
INSERT INTO public.line_item (id, inventory_item_id, quantity, shopping_cart_id)
VALUES (6, 21, 1, 4);

--FailCreateOrder
INSERT INTO public.shopping_cart (id, account_id)
VALUES (5, 18);
INSERT INTO public.line_item (id, inventory_item_id, quantity, shopping_cart_id)
VALUES (7, 22, 3, 5);

INSERT INTO public.shopping_cart (id, account_id)
VALUES (6, 19);
INSERT INTO public.line_item (id, inventory_item_id, quantity, shopping_cart_id)
VALUES (8, 23, 1, 6);

INSERT INTO public.shopping_cart (id, account_id)
VALUES (7, 20);
INSERT INTO public.line_item (id, inventory_item_id, quantity, shopping_cart_id)
VALUES (9, 24, 1, 7);

INSERT INTO public.shopping_cart (id, account_id)
VALUES (8, 21);
INSERT INTO public.line_item (id, inventory_item_id, quantity, shopping_cart_id)
VALUES (10, 25, 1, 8);

INSERT INTO public.shopping_cart (id, account_id)
VALUES (9, 22);
INSERT INTO public.line_item (id, inventory_item_id, quantity, shopping_cart_id)
VALUES (11, 25, 1, 9);

ALTER SEQUENCE shopping_cart_id_seq RESTART WITH 10;
ALTER SEQUENCE line_item_id_seq RESTART WITH 12;

