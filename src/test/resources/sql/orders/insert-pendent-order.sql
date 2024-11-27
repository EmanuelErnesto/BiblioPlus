INSERT INTO tb_orders (
    id,
    user_id,
    book_id,
    start_date,
    end_date,
    order_status,
    created_at,
    updated_at
)
VALUES (
    'e4bbeb00-6ecd-4f7b-ac76-65f9d8e6ce8a',
    '1c97fc13-93bb-4dde-8cf3-ba156f195f82',
    'd8f426fe-71f9-46ed-8d0b-b39a4f88db42',
    '2025-12-01',
    '2025-12-15',
    'DELIVERED',
    NOW(),
    NOW()
);
