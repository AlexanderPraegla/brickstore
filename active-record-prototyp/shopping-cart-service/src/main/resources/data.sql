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


ALTER SEQUENCE line_item_id_seq RESTART WITH 4;
ALTER SEQUENCE shopping_cart_id_seq RESTART WITH 3;

