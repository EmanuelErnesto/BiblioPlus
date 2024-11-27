INSERT INTO tb_orders (id, user_id, book_id, order_status, start_date, end_date, created_at, updated_at)
VALUES (
    '6c2b12d7-b18d-442f-a5d7-3b5ab3e21762',
    '1c97fc13-93bb-4dde-8cf3-ba156f195f82',
    'b1e72f2f-d5c8-44bc-a7fe-eeb62d0cf541',
    'RETURNED',
    '2024-11-01',
    '2024-12-10',
    CURRENT_TIMESTAMP(),
    CURRENT_TIMESTAMP()
);

INSERT INTO tb_orders (id, user_id, book_id, order_status, start_date, end_date, created_at, updated_at)
VALUES (
    'e5fbc8a1-0981-48cc-b8a5-b9256e2a079f',
    '5765c65e-2fd0-4df7-963c-2345c5f9f164',
    'd8f426fe-71f9-46ed-8d0b-b39a4f88db42',
    'RETURNED',
    '2024-11-05',
    '2024-11-15',
    CURRENT_TIMESTAMP(),
    CURRENT_TIMESTAMP()
);

INSERT INTO tb_orders (id, user_id, book_id, order_status, start_date, end_date, created_at, updated_at)
VALUES (
    'b9d1102b-78fa-45d9-b441-72ed05fc9b52',
    '29e186bc-3e2d-42c1-89b2-4097cb6c7c66',
    '8c2c78f3-5d33-44d2-a8de-896327a7b765',
    'RETURNED',
    '2024-11-12',
    '2024-11-20',
    CURRENT_TIMESTAMP(),
    CURRENT_TIMESTAMP()
);

INSERT INTO tb_books (id, name, release_date, description, quantity_in_stock, genre, publisher, created_at, updated_at, version)
VALUES
('f4b198bb-2a7e-43c2-9d63-e4a3e39cbe34',
 'Livro de Exemplo com Estoque Zero',
 '2024-11-26',
 'Este é um livro de exemplo criado com a quantidade de estoque igual a zero.',  -- Descrição
 0,
 'Ficção',
 'Editora Exemplo',
 NOW(),
 NOW(),
 1
);

