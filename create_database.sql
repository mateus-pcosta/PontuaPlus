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
    id              BIGINT      PRIMARY KEY,
    matricula       VARCHAR(255) NOT NULL UNIQUE,
    cpf             VARCHAR(255) NOT NULL UNIQUE,
    serie           VARCHAR(255) NOT NULL,
    colegio         VARCHAR(255) NOT NULL,
    turma           VARCHAR(255),
    data_nascimento DATE,
    data_ingresso   DATE,
    bimestre_atual  INT DEFAULT 2,
    FOREIGN KEY (id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_matricula (matricula)
);

-- 4. Tabela de colaboradores (professores, coordenadores, diretores)
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
-- Dados de teste — usuário inicial para desenvolvimento
-- Login: mateus@pontua.com | Senha: 123456
-- =============================================================

INSERT INTO usuarios (nome, email, senha, tipo) VALUES
('Mateus Pessoa Costa', 'mateus@pontua.com',
 '$2a$10$N9qo8uLOickgx2ZBRl.lU.fWXw5sZfB9hQQHvC3SRKu0EqAi7oLva',
 'ALUNO');

INSERT INTO alunos (id, matricula, cpf, serie, colegio, turma, data_nascimento, data_ingresso, bimestre_atual) VALUES
(1, '2024001', '12345678900', '9º Ano', 'Colégio Objetivo', '9A', '2009-05-15', '2024-01-01', 2);

-- Notas do 2º bimestre
-- Médias: 7.5 → 25 pontos (>= 7.0)
INSERT INTO notas (aluno_id, disciplina, valor, bimestre, pontos_conquistados) VALUES
(1, 'Matemática', 8.0, 2, 32),
(1, 'Inglês',     7.5, 2, 25),
(1, 'Português',  7.0, 2, 25),
(1, 'Ciências',   8.0, 2, 32),
(1, 'História',   7.0, 2, 25);

-- Frequências do 2º bimestre (junho e julho)
-- Junho: 18/20 = 90%  → 13 pontos
-- Julho: 17/20 = 85%  → 11 pontos
-- Média: 87.5%         → 11 pontos (>= 85%)
INSERT INTO frequencias (aluno_id, mes, ano, bimestre, total_aulas, presencas, faltas, percentual_frequencia, pontos_conquistados) VALUES
(1, 6, 2024, 2, 20, 18, 2, 90.0, 13),
(1, 7, 2024, 2, 20, 17, 3, 85.0, 11);

-- Atividades extracurriculares do 2º bimestre
-- LIDER_TURMA=10, SISTEMA_COMPANHEIROS=10, OBMEP=5, CRIACAO_CONTEUDO_AUDIO=15 → total 40
INSERT INTO atividades_extras (aluno_id, nome, tipo, bimestre, pontos_conquistados) VALUES
(1, 'Líder de Turma',               'LIDER_TURMA',              2, 10),
(1, 'Sistema de Companheiros',      'SISTEMA_COMPANHEIROS',     2, 10),
(1, 'Participação na OBMEP',        'OBMEP',                    2,  5),
(1, 'Criação de Conteúdo de Áudio', 'CRIACAO_CONTEUDO_AUDIO',   2, 15);

-- Pontuação consolidada do 2º bimestre
-- notas=25 + frequencia=11 + extras=40 = 76 → OURO (65–80)
INSERT INTO pontuacoes (aluno_id, pontos_notas, pontos_frequencia, pontos_extras, total_pontos, ranking, posicao_ranking) VALUES
(1, 25, 11, 40, 76, 'OURO', 1);
