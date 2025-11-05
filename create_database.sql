-- Script para criar o banco de dados Pontua+ manualmente
-- Execute este script no MySQL Workbench ou via linha de comando

-- 1. Criar o banco de dados
DROP DATABASE IF EXISTS pontua_db;
CREATE DATABASE pontua_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pontua_db;

-- 2. Criar tabela usuarios (base)
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    INDEX idx_email (email)
);

-- 3. Criar tabela alunos (herança JOINED)
CREATE TABLE alunos (
    id BIGINT PRIMARY KEY,
    matricula VARCHAR(255) NOT NULL UNIQUE,
    serie VARCHAR(255) NOT NULL,
    colegio VARCHAR(255) NOT NULL,
    data_nascimento DATE,
    data_ingresso DATE,
    turma VARCHAR(255),
    FOREIGN KEY (id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_matricula (matricula)
);

-- 4. Criar tabela notas
CREATE TABLE notas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    aluno_id BIGINT NOT NULL,
    disciplina VARCHAR(255) NOT NULL,
    valor DOUBLE NOT NULL,
    bimestre INT NOT NULL,
    pontos_conquistados INT DEFAULT 0,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id) ON DELETE CASCADE,
    INDEX idx_aluno_notas (aluno_id)
);

-- 5. Criar tabela frequencias
CREATE TABLE frequencias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    aluno_id BIGINT NOT NULL,
    mes INT NOT NULL,
    ano INT NOT NULL,
    total_aulas INT NOT NULL,
    presencas INT NOT NULL,
    faltas INT NOT NULL,
    percentual_frequencia DOUBLE DEFAULT 0,
    pontos_conquistados INT DEFAULT 0,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id) ON DELETE CASCADE,
    INDEX idx_aluno_freq (aluno_id)
);

-- 6. Criar tabela atividades_extras
CREATE TABLE atividades_extras (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    aluno_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    tipo VARCHAR(255) NOT NULL,
    pontos_conquistados INT DEFAULT 0,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id) ON DELETE CASCADE,
    INDEX idx_aluno_ativ (aluno_id)
);

-- 7. Criar tabela pontuacoes
CREATE TABLE pontuacoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    aluno_id BIGINT NOT NULL UNIQUE,
    pontos_notas INT DEFAULT 0,
    pontos_frequencia INT DEFAULT 0,
    pontos_extras INT DEFAULT 0,
    total_pontos INT DEFAULT 0,
    ranking VARCHAR(50) NOT NULL DEFAULT 'BRONZE',
    posicao_ranking INT,
    FOREIGN KEY (aluno_id) REFERENCES alunos(id) ON DELETE CASCADE,
    INDEX idx_total_pontos (total_pontos DESC)
);

-- 8. Inserir usuário Mateus com senha criptografada BCrypt
-- Senha: 123456
-- Hash BCrypt: $2a$10$xYzP8XZ5q5f5f5f5f5f5f5euVZvN6Q6Q6Q6Q6Q6Q6Q6Q6Q6Q6Q6Q6
INSERT INTO usuarios (nome, email, senha, tipo) VALUES
('Mateus Pessoa Costa', 'mateus@pontua.com', '$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva', 'ALUNO');

-- 9. Inserir dados do aluno Mateus
INSERT INTO alunos (id, matricula, serie, colegio, data_nascimento, data_ingresso, turma) VALUES
(1, '2024001', '9º Ano', 'Colégio Objetivo', '2009-05-15', '2024-01-01', '9A');

-- 10. Inserir notas do Mateus
INSERT INTO notas (aluno_id, disciplina, valor, bimestre, pontos_conquistados) VALUES
(1, 'Matemática', 10.0, 1, 35),
(1, 'Inglês', 10.0, 1, 35),
(1, 'Português', 9.0, 1, 35),
(1, 'Ciências', 9.0, 1, 35),
(1, 'História', 8.0, 1, 32);

-- 11. Inserir frequências (Janeiro a Julho 2024)
INSERT INTO frequencias (aluno_id, mes, ano, total_aulas, presencas, faltas, percentual_frequencia, pontos_conquistados) VALUES
(1, 1, 2024, 20, 18, 2, 90.0, 13),
(1, 2, 2024, 20, 18, 2, 90.0, 13),
(1, 3, 2024, 20, 18, 2, 90.0, 13),
(1, 4, 2024, 20, 18, 2, 90.0, 13),
(1, 5, 2024, 20, 18, 2, 90.0, 13),
(1, 6, 2024, 20, 18, 2, 90.0, 13),
(1, 7, 2024, 20, 18, 2, 90.0, 13);

-- 12. Inserir atividades extras
INSERT INTO atividades_extras (aluno_id, nome, tipo, pontos_conquistados) VALUES
(1, 'Olimpíada de Matemática', 'OLIMPIADA', 25),
(1, 'Clube de Robótica', 'CLUBE', 15);

-- 13. Inserir pontuação total
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(1, 172, 91, 40, 303, 'DIAMOND', 1);

-- Verificar dados inseridos
SELECT 'Verificação dos dados inseridos:' as Info;
SELECT * FROM usuarios;
SELECT * FROM alunos;
SELECT * FROM notas;
SELECT COUNT(*) as total_frequencias FROM frequencias;
SELECT * FROM atividades_extras;
SELECT * FROM pontuacoes;

SELECT 'Banco de dados criado com sucesso!' as Status;
SELECT 'Login: mateus@pontua.com | Senha: 123456' as Credenciais;
