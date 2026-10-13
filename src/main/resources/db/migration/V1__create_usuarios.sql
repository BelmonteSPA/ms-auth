CREATE TABLE usuarios (
    id_usuario    BIGINT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password      VARCHAR(100) NOT NULL,
    rol           VARCHAR(20)  NOT NULL,
    id_referencia BIGINT       NULL,
    activo        BOOLEAN      NOT NULL DEFAULT TRUE
);
