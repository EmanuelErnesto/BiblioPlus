INSERT INTO tb_address (id, cep, patio, complement, neighborhood, locality, uf, created_at, updated_at)
VALUES (
    '556726b0-1bda-42ed-b8c8-c6d4be14913f',
    '55850-000',
    'N/A',
    'N/A',
    'N/A',
    'Vicência',
    'PE',
    CURRENT_DATE(),
    CURRENT_DATE()
);

INSERT INTO tb_address (id, cep, patio, complement, neighborhood, locality, uf, created_at, updated_at)
VALUES ('baf0542c-8595-4c86-a530-fa038d6726d2','23456789', 'Rua B, 456', 'Casa 202', 'Jardim das Flores', 'Rio de Janeiro', 'RJ', CURRENT_DATE(), CURRENT_DATE());

INSERT INTO tb_address (id, cep, patio, complement, neighborhood, locality, uf, created_at, updated_at)
VALUES ('ee164a52-9d4b-41da-8407-a548522c2f6b','34567890', 'Rua C, 789', 'Casa 303', 'Vila Nova', 'Belo Horizonte', 'MG', CURRENT_DATE(), CURRENT_DATE());

INSERT INTO tb_users (id, name, email, cpf, birth_day, password, address_id, role)
VALUES ('29e186bc-3e2d-42c1-89b2-4097cb6c7c66', 'Maria', 'maria@gmail.com', '66228049232', '2002-07-03', '$2a$12$GGekDaHIsFT7od9Bxso9oOKLXlY/dQGnG0.U3hdbt8UMP3jj2an1S', '556726b0-1bda-42ed-b8c8-c6d4be14913f', 'ADMIN');

INSERT INTO tb_users (id, name, email, cpf, birth_day, password, address_id, role)
VALUES ('5765c65e-2fd0-4df7-963c-2345c5f9f164', 'Ana Carla', 'anacarla@gmail.com', '63928498150', '2004-01-03', '$2a$12$Ub.odytvGvhF96e2fiSaPOJyQZi9aUFXUPgbbmchlgzIURESEF6/m', 'ee164a52-9d4b-41da-8407-a548522c2f6b', 'ADMIN');

INSERT INTO tb_users (id, name, email, cpf, birth_day, password, address_id, role)
VALUES ('1c97fc13-93bb-4dde-8cf3-ba156f195f82', 'João', 'joao@gmail.com', '36176822432', '2000-12-10', '$2a$12$kk5NLnm4/e.YGls/km1f0e5VNiFJlNqITQ0sYojQ7OltCMAnPhWpS', 'baf0542c-8595-4c86-a530-fa038d6726d2', 'CLIENT');
