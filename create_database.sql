-- Script de inicialização do banco de dados Pontua+
-- Executado automaticamente pelo Docker na primeira inicialização
-- Pode também ser rodado manualmente no MySQL Workbench ou via linha de comando

-- 1. Criar o banco de dados
DROP DATABASE IF EXISTS pontua_db;
CREATE DATABASE pontua_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pontua_db;

-- 2. Tabela base de usuários (herança JOINED)
CREATE TABLE usuarios (
    id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome   VARCHAR(255) NOT NULL,
    email  VARCHAR(255) NOT NULL UNIQUE,
    senha  VARCHAR(255) NOT NULL,
    tipo   VARCHAR(50)  NOT NULL,
    INDEX idx_email (email)
);

-- 3. Tabela de alunos
CREATE TABLE alunos (
    id              BIGINT       PRIMARY KEY,
    matricula       VARCHAR(255) NOT NULL UNIQUE,
    cpf             VARCHAR(255) NOT NULL UNIQUE,
    serie           VARCHAR(255) NOT NULL,
    colegio         VARCHAR(255) NOT NULL,
    turma           VARCHAR(255),
    data_nascimento DATE,
    data_ingresso   DATE,
    bimestre_atual  INT DEFAULT 3,
    FOREIGN KEY (id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_matricula (matricula)
);

-- 4. Tabela de colaboradores
CREATE TABLE colaboradores (
    id              BIGINT       PRIMARY KEY,
    matricula       VARCHAR(255) NOT NULL UNIQUE,
    cpf             VARCHAR(255) NOT NULL UNIQUE,
    colegio         VARCHAR(255) NOT NULL,
    data_nascimento DATE,
    data_ingresso   DATE,
    FOREIGN KEY (id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_colaborador_matricula (matricula)
);

-- 5. Tabela de notas
CREATE TABLE notas (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    aluno_id            BIGINT         NOT NULL,
    disciplina          VARCHAR(255)   NOT NULL,
    valor               DOUBLE         NOT NULL,
    bimestre            INT            NOT NULL,
    pontos_conquistados INT DEFAULT 0,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id) ON DELETE CASCADE,
    INDEX idx_aluno_notas (aluno_id)
);

-- 6. Tabela de frequências
CREATE TABLE frequencias (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    aluno_id              BIGINT NOT NULL,
    mes                   INT    NOT NULL,
    ano                   INT    NOT NULL,
    bimestre              INT    NOT NULL,
    total_aulas           INT    NOT NULL,
    presencas             INT    NOT NULL,
    faltas                INT    NOT NULL,
    percentual_frequencia DOUBLE DEFAULT 0,
    pontos_conquistados   INT    DEFAULT 0,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id) ON DELETE CASCADE,
    INDEX idx_aluno_freq (aluno_id)
);

-- 7. Tabela de atividades extracurriculares
CREATE TABLE atividades_extras (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    aluno_id            BIGINT       NOT NULL,
    nome                VARCHAR(255) NOT NULL,
    tipo                VARCHAR(255) NOT NULL,
    bimestre            INT          NOT NULL,
    pontos_conquistados INT DEFAULT 0,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id) ON DELETE CASCADE,
    INDEX idx_aluno_ativ (aluno_id)
);

-- 8. Tabela de pontuações
CREATE TABLE pontuacoes (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    aluno_id          BIGINT      NOT NULL UNIQUE,
    pontos_notas      INT DEFAULT 0,
    pontos_frequencia INT DEFAULT 0,
    pontos_extras     INT DEFAULT 0,
    total_pontos      INT DEFAULT 0,
    ranking           VARCHAR(50) NOT NULL DEFAULT 'BRONZE',
    posicao_ranking   INT,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id) ON DELETE CASCADE,
    INDEX idx_total_pontos (total_pontos DESC)
);

-- =============================================================
-- Usuário inicial — Login: mateus@pontua.com | Senha: 123456
-- =============================================================

INSERT INTO usuarios (nome, email, senha, tipo) VALUES
('Mateus Pessoa Costa', 'mateus@pontua.com',
 '$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva', 'ALUNO');

INSERT INTO alunos (id, matricula, cpf, serie, colegio, turma, data_nascimento, data_ingresso, bimestre_atual) VALUES
(1, '2024001', '12345678900', '9º Ano', 'Colégio Objetivo', '9A', '2009-05-15', '2024-01-01', 3);

-- =============================================================
-- Alunos fictícios — bimestre_atual=3 | Senha de todos: 123456
-- =============================================================

-- ---- DIAMOND (81–100 pts) ----
INSERT INTO usuarios (nome, email, senha, tipo) VALUES
('Ana Carolina Silva', 'ana.silva@pontua.com',  '$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva', 'ALUNO'),
('Rafael Souza Lima',  'rafael.lima@pontua.com','$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva', 'ALUNO'),
('Isabela Martins',    'isabela.m@pontua.com',  '$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva', 'ALUNO');

INSERT INTO alunos (id, matricula, cpf, serie, colegio, turma, bimestre_atual) VALUES
(2, '2024002', '22222222200', '9º Ano', 'Colégio Objetivo', '9A', 3),
(3, '2024003', '33333333300', '9º Ano', 'Colégio Objetivo', '9B', 3),
(4, '2024004', '44444444400', '8º Ano', 'Colégio Objetivo', '8A', 3);

-- ---- OURO (65–80 pts) ----
INSERT INTO usuarios (nome, email, senha, tipo) VALUES
('Lucas Ferreira',  'lucas.f@pontua.com',    '$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva', 'ALUNO'),
('Camila Rodrigues','camila.r@pontua.com',   '$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva', 'ALUNO'),
('Pedro Alves',     'pedro.a@pontua.com',    '$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva', 'ALUNO'),
('Fernanda Costa',  'fernanda.c@pontua.com', '$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva', 'ALUNO'),
('Gabriel Mendes',  'gabriel.m@pontua.com',  '$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva', 'ALUNO');

INSERT INTO alunos (id, matricula, cpf, serie, colegio, turma, bimestre_atual) VALUES
(5, '2024005', '55555555500', '9º Ano', 'Colégio Objetivo', '9A', 3),
(6, '2024006', '66666666600', '8º Ano', 'Colégio Objetivo', '8B', 3),
(7, '2024007', '77777777700', '9º Ano', 'Colégio Objetivo', '9B', 3),
(8, '2024008', '88888888800', '7º Ano', 'Colégio Objetivo', '7A', 3),
(9, '2024009', '99999999900', '8º Ano', 'Colégio Objetivo', '8A', 3);

-- ---- PRATA (26–64 pts) ----
INSERT INTO usuarios (nome, email, senha, tipo) VALUES
('Beatriz Oliveira',  'beatriz.o@pontua.com',  '$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva', 'ALUNO'),
('Thiago Nascimento', 'thiago.n@pontua.com',   '$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva', 'ALUNO'),
('Juliana Pereira',   'juliana.p@pontua.com',  '$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva', 'ALUNO'),
('Vinícius Santos',   'vinicius.s@pontua.com', '$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva', 'ALUNO');

INSERT INTO alunos (id, matricula, cpf, serie, colegio, turma, bimestre_atual) VALUES
(10, '2024010', '10101010100', '7º Ano', 'Colégio Objetivo', '7B', 3),
(11, '2024011', '11111111100', '6º Ano', 'Colégio Objetivo', '6A', 3),
(12, '2024012', '12121212100', '8º Ano', 'Colégio Objetivo', '8B', 3),
(13, '2024013', '13131313100', '7º Ano', 'Colégio Objetivo', '7A', 3);

-- ---- BRONZE (0–25 pts) ----
INSERT INTO usuarios (nome, email, senha, tipo) VALUES
('Larissa Carvalho', 'larissa.c@pontua.com', '$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva', 'ALUNO'),
('Diego Barbosa',    'diego.b@pontua.com',   '$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva', 'ALUNO'),
('Natália Gomes',    'natalia.g@pontua.com', '$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva', 'ALUNO'),
('Felipe Araujo',    'felipe.a@pontua.com',  '$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva', 'ALUNO');

INSERT INTO alunos (id, matricula, cpf, serie, colegio, turma, bimestre_atual) VALUES
(14, '2024014', '14141414100', '6º Ano', 'Colégio Objetivo', '6B', 3),
(15, '2024015', '15151515100', '6º Ano', 'Colégio Objetivo', '6A', 3),
(16, '2024016', '16161616100', '6º Ano', 'Colégio Objetivo', '6B', 3),
(17, '2024017', '17171717100', '7º Ano', 'Colégio Objetivo', '7B', 3);

-- =============================================================
-- Notas — 3º bimestre (agosto–setembro)
-- Mesmas disciplinas do perfil de cada aluno
-- =============================================================
INSERT INTO notas (aluno_id, disciplina, valor, bimestre, pontos_conquistados) VALUES
-- Mateus: Matemática, Inglês, Português, Ciências, História | avg 7.88 → 25pts
(1,'Matemática',7.8,3,0),(1,'Inglês',8.0,3,0),(1,'Português',7.5,3,0),(1,'Ciências',8.2,3,0),(1,'História',7.9,3,0),
-- Ana: Matemática, Português, Ciências, História | avg 9.375 → 35pts
(2,'Matemática',9.4,3,0),(2,'Português',9.6,3,0),(2,'Ciências',9.2,3,0),(2,'História',9.3,3,0),
-- Rafael: Matemática, Português, Ciências, Inglês | avg 9.075 → 35pts
(3,'Matemática',9.1,3,0),(3,'Português',8.9,3,0),(3,'Ciências',9.3,3,0),(3,'Inglês',9.0,3,0),
-- Isabela: Matemática, Português, Ciências, Inglês | avg 9.125 → 35pts
(4,'Matemática',9.2,3,0),(4,'Português',9.0,3,0),(4,'Ciências',8.9,3,0),(4,'Inglês',9.4,3,0),
-- Lucas: Matemática, Português, Ciências, História | avg 8.2 → 32pts
(5,'Matemática',8.3,3,0),(5,'Português',8.0,3,0),(5,'Ciências',8.5,3,0),(5,'História',8.0,3,0),
-- Camila: Matemática, Português, Ciências, Inglês | avg 7.6 → 25pts
(6,'Matemática',7.8,3,0),(6,'Português',7.5,3,0),(6,'Ciências',7.6,3,0),(6,'Inglês',7.5,3,0),
-- Pedro: Matemática, Português, Ciências, História | avg 7.5 → 25pts
(7,'Matemática',7.5,3,0),(7,'Português',7.6,3,0),(7,'Ciências',7.3,3,0),(7,'História',7.6,3,0),
-- Fernanda: Matemática, Português, Ciências, Inglês | avg 8.0 → 32pts
(8,'Matemática',8.0,3,0),(8,'Português',8.2,3,0),(8,'Ciências',7.8,3,0),(8,'Inglês',8.0,3,0),
-- Gabriel: Matemática, Português, Ciências, Inglês | avg 8.0 → 32pts
(9,'Matemática',8.2,3,0),(9,'Português',7.9,3,0),(9,'Ciências',8.0,3,0),(9,'Inglês',7.9,3,0),
-- Beatriz: Matemática, Português, Ciências, Inglês | avg 7.0 → 25pts
(10,'Matemática',7.0,3,0),(10,'Português',7.2,3,0),(10,'Ciências',6.8,3,0),(10,'Inglês',7.0,3,0),
-- Thiago: Matemática, Português, Ciências, Inglês | avg 6.3 → 20pts
(11,'Matemática',6.5,3,0),(11,'Português',6.0,3,0),(11,'Ciências',6.5,3,0),(11,'Inglês',6.2,3,0),
-- Juliana: Matemática, Português, Ciências, Inglês | avg 6.2 → 20pts
(12,'Matemática',6.0,3,0),(12,'Português',6.5,3,0),(12,'Ciências',6.0,3,0),(12,'Inglês',6.3,3,0),
-- Vinícius: Matemática, Português, Ciências, Inglês | avg 5.5 → 15pts
(13,'Matemática',5.5,3,0),(13,'Português',5.8,3,0),(13,'Ciências',5.5,3,0),(13,'Inglês',5.2,3,0),
-- Larissa: Matemática, Português, Ciências, Inglês | avg 6.0 → 20pts
(14,'Matemática',6.0,3,0),(14,'Português',6.2,3,0),(14,'Ciências',5.8,3,0),(14,'Inglês',6.0,3,0),
-- Diego: Matemática, Português, Ciências, Inglês | avg 4.0 → 0pts
(15,'Matemática',4.0,3,0),(15,'Português',4.5,3,0),(15,'Ciências',3.8,3,0),(15,'Inglês',3.7,3,0),
-- Natália: Matemática, Português, Ciências, Inglês | avg 3.0 → 0pts
(16,'Matemática',3.0,3,0),(16,'Português',3.2,3,0),(16,'Ciências',2.8,3,0),(16,'Inglês',3.0,3,0),
-- Felipe: Matemática, Português, Ciências, Inglês | avg 5.0 → 15pts
(17,'Matemática',5.0,3,0),(17,'Português',4.8,3,0),(17,'Ciências',5.5,3,0),(17,'Inglês',4.7,3,0);

-- =============================================================
-- Notas — 4º bimestre (outubro–dezembro)
-- =============================================================
INSERT INTO notas (aluno_id, disciplina, valor, bimestre, pontos_conquistados) VALUES
(1,'Matemática',8.0,4,0),(1,'Inglês',8.2,4,0),(1,'Português',8.0,4,0),(1,'Ciências',8.5,4,0),(1,'História',7.9,4,0),
(2,'Matemática',9.5,4,0),(2,'Português',9.7,4,0),(2,'Ciências',9.4,4,0),(2,'História',9.5,4,0),
(3,'Matemática',9.3,4,0),(3,'Português',9.0,4,0),(3,'Ciências',9.1,4,0),(3,'Inglês',9.2,4,0),
(4,'Matemática',9.0,4,0),(4,'Português',9.3,4,0),(4,'Ciências',8.8,4,0),(4,'Inglês',9.1,4,0),
(5,'Matemática',7.8,4,0),(5,'Português',8.0,4,0),(5,'Ciências',8.2,4,0),(5,'História',7.6,4,0),
(6,'Matemática',8.0,4,0),(6,'Português',7.8,4,0),(6,'Ciências',8.2,4,0),(6,'Inglês',8.0,4,0),
(7,'Matemática',7.8,4,0),(7,'Português',8.0,4,0),(7,'Ciências',7.5,4,0),(7,'História',8.0,4,0),
(8,'Matemática',7.8,4,0),(8,'Português',7.5,4,0),(8,'Ciências',7.9,4,0),(8,'Inglês',7.8,4,0),
(9,'Matemática',8.5,4,0),(9,'Português',8.0,4,0),(9,'Ciências',8.3,4,0),(9,'Inglês',8.5,4,0),
(10,'Matemática',6.8,4,0),(10,'Português',7.0,4,0),(10,'Ciências',6.5,4,0),(10,'Inglês',6.9,4,0),
(11,'Matemática',6.5,4,0),(11,'Português',6.8,4,0),(11,'Ciências',6.0,4,0),(11,'Inglês',6.7,4,0),
(12,'Matemática',5.8,4,0),(12,'Português',6.0,4,0),(12,'Ciências',6.0,4,0),(12,'Inglês',5.7,4,0),
(13,'Matemática',5.8,4,0),(13,'Português',6.0,4,0),(13,'Ciências',5.5,4,0),(13,'Inglês',5.7,4,0),
(14,'Matemática',5.5,4,0),(14,'Português',6.0,4,0),(14,'Ciências',5.0,4,0),(14,'Inglês',5.5,4,0),
(15,'Matemática',3.5,4,0),(15,'Português',3.8,4,0),(15,'Ciências',4.0,4,0),(15,'Inglês',3.5,4,0),
(16,'Matemática',2.5,4,0),(16,'Português',2.8,4,0),(16,'Ciências',3.0,4,0),(16,'Inglês',2.5,4,0),
(17,'Matemática',4.5,4,0),(17,'Português',4.8,4,0),(17,'Ciências',5.0,4,0),(17,'Inglês',4.3,4,0);

-- =============================================================
-- Frequências — agosto a dezembro (bim3: ago/set · bim4: out/nov/dez)
-- =============================================================
INSERT INTO frequencias (aluno_id, mes, ano, bimestre, total_aulas, presencas, faltas, percentual_frequencia, pontos_conquistados) VALUES
-- Mateus (OURO): bim3 avg 90% → 13pts
(1, 8,2024,3,20,18,2, 90.0,0),(1, 9,2024,3,20,18,2, 90.0,0),
(1,10,2024,4,20,18,2, 90.0,0),(1,11,2024,4,20,19,1, 95.0,0),(1,12,2024,4,20,17,3, 85.0,0),
-- Ana (DIAMOND): bim3 avg 97.5% → 15pts
(2, 8,2024,3,20,20,0,100.0,0),(2, 9,2024,3,20,19,1, 95.0,0),
(2,10,2024,4,20,20,0,100.0,0),(2,11,2024,4,20,20,0,100.0,0),(2,12,2024,4,20,19,1, 95.0,0),
-- Rafael (DIAMOND): bim3 avg 95% → 15pts
(3, 8,2024,3,20,20,0,100.0,0),(3, 9,2024,3,20,18,2, 90.0,0),
(3,10,2024,4,20,19,1, 95.0,0),(3,11,2024,4,20,20,0,100.0,0),(3,12,2024,4,20,18,2, 90.0,0),
-- Isabela (DIAMOND): bim3 avg 95% → 15pts
(4, 8,2024,3,20,19,1, 95.0,0),(4, 9,2024,3,20,19,1, 95.0,0),
(4,10,2024,4,20,20,0,100.0,0),(4,11,2024,4,20,19,1, 95.0,0),(4,12,2024,4,20,19,1, 95.0,0),
-- Lucas (OURO): bim3 avg 92.5% → 13pts
(5, 8,2024,3,20,18,2, 90.0,0),(5, 9,2024,3,20,19,1, 95.0,0),
(5,10,2024,4,20,17,3, 85.0,0),(5,11,2024,4,20,18,2, 90.0,0),(5,12,2024,4,20,18,2, 90.0,0),
-- Camila (OURO): bim3 avg 87.5% → 11pts
(6, 8,2024,3,20,17,3, 85.0,0),(6, 9,2024,3,20,18,2, 90.0,0),
(6,10,2024,4,20,18,2, 90.0,0),(6,11,2024,4,20,17,3, 85.0,0),(6,12,2024,4,20,17,3, 85.0,0),
-- Pedro (OURO): bim3 avg 85% → 11pts
(7, 8,2024,3,20,17,3, 85.0,0),(7, 9,2024,3,20,17,3, 85.0,0),
(7,10,2024,4,20,17,3, 85.0,0),(7,11,2024,4,20,16,4, 80.0,0),(7,12,2024,4,20,17,3, 85.0,0),
-- Fernanda (OURO): bim3 avg 82.5% → 9pts
(8, 8,2024,3,20,16,4, 80.0,0),(8, 9,2024,3,20,17,3, 85.0,0),
(8,10,2024,4,20,16,4, 80.0,0),(8,11,2024,4,20,16,4, 80.0,0),(8,12,2024,4,20,15,5, 75.0,0),
-- Gabriel (OURO): bim3 avg 87.5% → 11pts
(9, 8,2024,3,20,18,2, 90.0,0),(9, 9,2024,3,20,17,3, 85.0,0),
(9,10,2024,4,20,18,2, 90.0,0),(9,11,2024,4,20,18,2, 90.0,0),(9,12,2024,4,20,17,3, 85.0,0),
-- Beatriz (PRATA): bim3 avg 80% → 9pts
(10, 8,2024,3,20,16,4, 80.0,0),(10, 9,2024,3,20,16,4, 80.0,0),
(10,10,2024,4,20,16,4, 80.0,0),(10,11,2024,4,20,15,5, 75.0,0),(10,12,2024,4,20,16,4, 80.0,0),
-- Thiago (PRATA): bim3 avg 77.5% → 7pts
(11, 8,2024,3,20,16,4, 80.0,0),(11, 9,2024,3,20,15,5, 75.0,0),
(11,10,2024,4,20,16,4, 80.0,0),(11,11,2024,4,20,15,5, 75.0,0),(11,12,2024,4,20,15,5, 75.0,0),
-- Juliana (PRATA): bim3 avg 77.5% → 7pts
(12, 8,2024,3,20,15,5, 75.0,0),(12, 9,2024,3,20,16,4, 80.0,0),
(12,10,2024,4,20,15,5, 75.0,0),(12,11,2024,4,20,15,5, 75.0,0),(12,12,2024,4,20,16,4, 80.0,0),
-- Vinícius (PRATA): bim3 avg 75% → 7pts
(13, 8,2024,3,20,15,5, 75.0,0),(13, 9,2024,3,20,15,5, 75.0,0),
(13,10,2024,4,20,14,6, 70.0,0),(13,11,2024,4,20,15,5, 75.0,0),(13,12,2024,4,20,15,5, 75.0,0),
-- Larissa (BRONZE): bim3 avg 72.5% → 0pts
(14, 8,2024,3,20,15,5, 75.0,0),(14, 9,2024,3,20,14,6, 70.0,0),
(14,10,2024,4,20,14,6, 70.0,0),(14,11,2024,4,20,13,7, 65.0,0),(14,12,2024,4,20,14,6, 70.0,0),
-- Diego (BRONZE): bim3 avg 62.5% → 0pts
(15, 8,2024,3,20,12,8, 60.0,0),(15, 9,2024,3,20,13,7, 65.0,0),
(15,10,2024,4,20,12,8, 60.0,0),(15,11,2024,4,20,12,8, 60.0,0),(15,12,2024,4,20,13,7, 65.0,0),
-- Natália (BRONZE): bim3 avg 52.5% → 0pts
(16, 8,2024,3,20,10,10, 50.0,0),(16, 9,2024,3,20,11,9, 55.0,0),
(16,10,2024,4,20,10,10, 50.0,0),(16,11,2024,4,20,10,10, 50.0,0),(16,12,2024,4,20,11,9, 55.0,0),
-- Felipe (BRONZE): bim3 avg 65% → 0pts
(17, 8,2024,3,20,13,7, 65.0,0),(17, 9,2024,3,20,13,7, 65.0,0),
(17,10,2024,4,20,12,8, 60.0,0),(17,11,2024,4,20,14,6, 70.0,0),(17,12,2024,4,20,13,7, 65.0,0);

-- =============================================================
-- Atividades extracurriculares — 3º bimestre
-- =============================================================
-- Mateus: 10+10+5+15 = 40pts
INSERT INTO atividades_extras (aluno_id, nome, tipo, bimestre, pontos_conquistados) VALUES
(1,'Líder de Turma','LIDER_TURMA',3,10),
(1,'Sistema de Companheiros','SISTEMA_COMPANHEIROS',3,10),
(1,'Participação na OBMEP','OBMEP',3,5),
(1,'Criação de Conteúdo de Áudio','CRIACAO_CONTEUDO_AUDIO',3,15);

-- Ana: 10+5+15+10+10 = 50pts
INSERT INTO atividades_extras (aluno_id, nome, tipo, bimestre, pontos_conquistados) VALUES
(2,'Líder de Turma','LIDER_TURMA',3,10),
(2,'OBMEP','OBMEP',3,5),
(2,'Podcast Educativo','CRIACAO_CONTEUDO_AUDIO',3,15),
(2,'Voluntariado','VOLUNTARIADO',3,10),
(2,'Clube de Robótica','CLUBE_ROBOTICA',3,10);

-- Rafael: 10+10+10+5 = 35pts
INSERT INTO atividades_extras (aluno_id, nome, tipo, bimestre, pontos_conquistados) VALUES
(3,'Sistema de Companheiros','SISTEMA_COMPANHEIROS',3,10),
(3,'Clube de Debate','CLUBE_DEBATE',3,10),
(3,'Conteúdo Escrito','CRIACAO_CONTEUDO_ESCRITO',3,10),
(3,'Olimpíada de Matemática','OLIMPIADA_MATEMATICA',3,5);

-- Isabela: 10+10+5+10 = 35pts
INSERT INTO atividades_extras (aluno_id, nome, tipo, bimestre, pontos_conquistados) VALUES
(4,'Voluntariado','VOLUNTARIADO',3,10),
(4,'Clube de Ciências','CLUBE_CIENCIAS',3,10),
(4,'OBMEP','OBMEP',3,5),
(4,'Líder de Turma','LIDER_TURMA',3,10);

-- Lucas: 10+10+10+5 = 35pts
INSERT INTO atividades_extras (aluno_id, nome, tipo, bimestre, pontos_conquistados) VALUES
(5,'Líder de Turma','LIDER_TURMA',3,10),
(5,'Clube de Debate','CLUBE_DEBATE',3,10),
(5,'Voluntariado','VOLUNTARIADO',3,10),
(5,'OBMEP','OBMEP',3,5);

-- Camila: 10+10+10 = 30pts
INSERT INTO atividades_extras (aluno_id, nome, tipo, bimestre, pontos_conquistados) VALUES
(6,'Sistema de Companheiros','SISTEMA_COMPANHEIROS',3,10),
(6,'Clube de Robótica','CLUBE_ROBOTICA',3,10),
(6,'Conteúdo Escrito','CRIACAO_CONTEUDO_ESCRITO',3,10);

-- Pedro: 10+5+10+10 = 35pts
INSERT INTO atividades_extras (aluno_id, nome, tipo, bimestre, pontos_conquistados) VALUES
(7,'Voluntariado','VOLUNTARIADO',3,10),
(7,'OBMEP','OBMEP',3,5),
(7,'Clube de Ciências','CLUBE_CIENCIAS',3,10),
(7,'Conteúdo Escrito','CRIACAO_CONTEUDO_ESCRITO',3,10);

-- Fernanda: 10+5+10 = 25pts
INSERT INTO atividades_extras (aluno_id, nome, tipo, bimestre, pontos_conquistados) VALUES
(8,'Clube de Debate','CLUBE_DEBATE',3,10),
(8,'Olimpíada de Matemática','OLIMPIADA_MATEMATICA',3,5),
(8,'Voluntariado','VOLUNTARIADO',3,10);

-- Gabriel: 10+5+10 = 25pts
INSERT INTO atividades_extras (aluno_id, nome, tipo, bimestre, pontos_conquistados) VALUES
(9,'Líder de Turma','LIDER_TURMA',3,10),
(9,'OBMEP','OBMEP',3,5),
(9,'Clube de Robótica','CLUBE_ROBOTICA',3,10);

-- Beatriz: 10+10 = 20pts
INSERT INTO atividades_extras (aluno_id, nome, tipo, bimestre, pontos_conquistados) VALUES
(10,'Voluntariado','VOLUNTARIADO',3,10),
(10,'Clube de Ciências','CLUBE_CIENCIAS',3,10);

-- Thiago: 5+10 = 15pts
INSERT INTO atividades_extras (aluno_id, nome, tipo, bimestre, pontos_conquistados) VALUES
(11,'Olimpíada de Matemática','OLIMPIADA_MATEMATICA',3,5),
(11,'Clube de Debate','CLUBE_DEBATE',3,10);

-- Juliana: 10pts
INSERT INTO atividades_extras (aluno_id, nome, tipo, bimestre, pontos_conquistados) VALUES
(12,'Voluntariado','VOLUNTARIADO',3,10);

-- Vinícius: 10pts
INSERT INTO atividades_extras (aluno_id, nome, tipo, bimestre, pontos_conquistados) VALUES
(13,'Clube de Robótica','CLUBE_ROBOTICA',3,10);

-- Diego: 10+5 = 15pts
INSERT INTO atividades_extras (aluno_id, nome, tipo, bimestre, pontos_conquistados) VALUES
(15,'Clube de Debate','CLUBE_DEBATE',3,10),
(15,'OBMEP','OBMEP',3,5);

-- Felipe: 5pts
INSERT INTO atividades_extras (aluno_id, nome, tipo, bimestre, pontos_conquistados) VALUES
(17,'OBMEP','OBMEP',3,5);

-- =============================================================
-- Pontuações — calculadas com base no 3º bimestre (bimestre_atual=3)
-- notas_pts: avg das notas bim3 mapeada para pontos
-- freq_pts:  avg das frequencias bim3 mapeada para pontos
-- extras:    soma das atividades_extras bim3 (cap 50)
-- =============================================================
-- Mateus:   notas 7.88→25 + freq 90%→13 + extras 40 = 78 → OURO
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(1, 25, 13, 40, 78, 'OURO', 5);

-- Ana:      notas 9.375→35 + freq 97.5%→15 + extras 50 = 100 → DIAMOND
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(2, 35, 15, 50, 100, 'DIAMOND', 1);

-- Rafael:   notas 9.075→35 + freq 95%→15 + extras 35 = 85 → DIAMOND
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(3, 35, 15, 35, 85, 'DIAMOND', 2);

-- Isabela:  notas 9.125→35 + freq 95%→15 + extras 35 = 85 → DIAMOND
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(4, 35, 15, 35, 85, 'DIAMOND', 3);

-- Lucas:    notas 8.2→32 + freq 92.5%→13 + extras 35 = 80 → OURO
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(5, 32, 13, 35, 80, 'OURO', 4);

-- Camila:   notas 7.6→25 + freq 87.5%→11 + extras 30 = 66 → OURO
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(6, 25, 11, 30, 66, 'OURO', 8);

-- Pedro:    notas 7.5→25 + freq 85%→11 + extras 35 = 71 → OURO
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(7, 25, 11, 35, 71, 'OURO', 6);

-- Fernanda: notas 8.0→32 + freq 82.5%→9 + extras 25 = 66 → OURO
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(8, 32, 9, 25, 66, 'OURO', 9);

-- Gabriel:  notas 8.0→32 + freq 87.5%→11 + extras 25 = 68 → OURO
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(9, 32, 11, 25, 68, 'OURO', 7);

-- Beatriz:  notas 7.0→25 + freq 80%→9 + extras 20 = 54 → PRATA
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(10, 25, 9, 20, 54, 'PRATA', 10);

-- Thiago:   notas 6.3→20 + freq 77.5%→7 + extras 15 = 42 → PRATA
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(11, 20, 7, 15, 42, 'PRATA', 11);

-- Juliana:  notas 6.2→20 + freq 77.5%→7 + extras 10 = 37 → PRATA
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(12, 20, 7, 10, 37, 'PRATA', 12);

-- Vinícius: notas 5.5→15 + freq 75%→7 + extras 10 = 32 → PRATA
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(13, 15, 7, 10, 32, 'PRATA', 13);

-- Larissa:  notas 6.0→20 + freq 72.5%→0 + extras 0 = 20 → BRONZE
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(14, 20, 0, 0, 20, 'BRONZE', 14);

-- Diego:    notas 4.0→0 + freq 62.5%→0 + extras 15 = 15 → BRONZE
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(15, 0, 0, 15, 15, 'BRONZE', 16);

-- Natália:  notas 3.0→0 + freq 52.5%→0 + extras 0 = 0 → BRONZE
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(16, 0, 0, 0, 0, 'BRONZE', 17);

-- Felipe:   notas 5.0→15 + freq 65%→0 + extras 5 = 20 → BRONZE
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(17, 15, 0, 5, 20, 'BRONZE', 15);

-- Posições finais por total_pontos DESC:
-- 1:Ana(100) 2:Rafael(85) 3:Isabela(85) 4:Lucas(80) 5:Mateus(78)
-- 6:Pedro(71) 7:Gabriel(68) 8:Camila(66) 9:Fernanda(66) 10:Beatriz(54)
-- 11:Thiago(42) 12:Juliana(37) 13:Vinícius(32) 14:Larissa(20) 15:Felipe(20)
-- 16:Diego(15) 17:Natália(0)
UPDATE pontuacoes SET posicao_ranking = 1  WHERE aluno_id = 2;
UPDATE pontuacoes SET posicao_ranking = 2  WHERE aluno_id = 3;
UPDATE pontuacoes SET posicao_ranking = 3  WHERE aluno_id = 4;
UPDATE pontuacoes SET posicao_ranking = 4  WHERE aluno_id = 5;
UPDATE pontuacoes SET posicao_ranking = 5  WHERE aluno_id = 1;
UPDATE pontuacoes SET posicao_ranking = 6  WHERE aluno_id = 7;
UPDATE pontuacoes SET posicao_ranking = 7  WHERE aluno_id = 9;
UPDATE pontuacoes SET posicao_ranking = 8  WHERE aluno_id = 6;
UPDATE pontuacoes SET posicao_ranking = 9  WHERE aluno_id = 8;
UPDATE pontuacoes SET posicao_ranking = 10 WHERE aluno_id = 10;
UPDATE pontuacoes SET posicao_ranking = 11 WHERE aluno_id = 11;
UPDATE pontuacoes SET posicao_ranking = 12 WHERE aluno_id = 12;
UPDATE pontuacoes SET posicao_ranking = 13 WHERE aluno_id = 13;
UPDATE pontuacoes SET posicao_ranking = 14 WHERE aluno_id = 14;
UPDATE pontuacoes SET posicao_ranking = 15 WHERE aluno_id = 17;
UPDATE pontuacoes SET posicao_ranking = 16 WHERE aluno_id = 15;
UPDATE pontuacoes SET posicao_ranking = 17 WHERE aluno_id = 16;
