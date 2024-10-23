
delete from product;
delete from  category;

INSERT INTO category (id, description, name) VALUES (1, 'Computer Keyboards', 'Keyboards');

INSERT INTO product (id, available_quantity, description, name, price, category_id)
VALUES
    (1, 10, 'Mechanical keyboard with RGB lighting', 'Mechanical Keyboard 1', 99.99, (SELECT id FROM category WHERE name = 'Keyboards')),
    (2, 15, 'Wireless compact keyboard', 'Wireless Compact Keyboard 1', 79.99, (SELECT id FROM category WHERE name = 'Keyboards'));