# Diagrama de Arquitetura — Pontua+

## Visão Geral das Camadas

```mermaid
flowchart TD
    subgraph Frontend["Frontend (Static Resources)"]
        direction LR
        A1[index.html]
        A2[login.html]
        A3[dashboard.html]
        A4[perfil.html]
        A5[registro.html]
        A6[dashboard.js / perfil.js / registro.js]
        A7[styles.css]
    end

    subgraph Backend["Backend (Spring Boot)"]
        direction TB

        subgraph Controllers["Controller Layer"]
            C1[AuthController\n/auth/login]
            C2[DashboardController\n/dashboard]
            C3[RegistroController\n/registro]
        end

        subgraph Services["Service Layer"]
            S1[AlunoService]
            S2[PontuacaoService]
            S3[CustomUserDetailsService]
        end

        subgraph Repositories["Repository Layer (Spring Data JPA)"]
            R1[AlunoRepository]
            R2[NotaRepository]
            R3[FrequenciaRepository]
            R4[AtividadeExtraRepository]
            R5[PontuacaoRepository]
            R6[UsuarioRepository]
        end

        subgraph Config["Config"]
            CF1[SecurityConfig\nSpring Security]
            CF2[DataInitializer\nDados de Teste]
        end
    end

    subgraph DB["Banco de Dados (MySQL 8.0+)"]
        direction LR
        D1[(pontua_db)]
    end

    Frontend -->|HTTP Requests| Controllers
    Controllers --> Services
    Services --> Repositories
    Repositories -->|JPA / Hibernate| DB
    Config -.->|Configura| Controllers
    Config -.->|Popula ao iniciar| DB
```

---

## Fluxo de Requisição

```mermaid
sequenceDiagram
    actor Aluno
    participant HTML as Frontend (HTML/JS)
    participant Auth as AuthController
    participant Dash as DashboardController
    participant PS as PontuacaoService
    participant DB as MySQL

    Aluno->>HTML: Acessa /login.html
    HTML->>Auth: POST /auth/login {email, senha}
    Auth->>DB: Busca usuário por email
    DB-->>Auth: Retorna Usuario
    Auth-->>HTML: 200 OK + dados do aluno

    Aluno->>HTML: Acessa /dashboard.html
    HTML->>Dash: GET /dashboard (autenticado)
    Dash->>PS: calcularPontuacao(aluno)
    PS->>DB: Busca Notas, Frequencias, AtividadesExtras por bimestre
    DB-->>PS: Retorna registros
    PS->>PS: Calcula pontos e ranking
    PS->>DB: Salva Pontuacao atualizada
    Dash-->>HTML: Retorna DashboardDTO
    HTML-->>Aluno: Exibe dashboard com pontos e ranking
```

---

## Organização dos Pacotes

```
com.pontuaplus.pontua_plus/
│
├── config/
│   ├── SecurityConfig.java       → Configuração do Spring Security
│   └── DataInitializer.java      → Carga inicial de dados de teste
│
├── controller/
│   ├── AuthController.java       → POST /auth/login
│   ├── DashboardController.java  → GET /dashboard
│   └── RegistroController.java   → POST /registro
│
├── dto/
│   ├── AlunoDTO.java
│   ├── DashboardDTO.java
│   └── RegistroAlunoDTO.java
│
├── entity/
│   ├── Usuario.java              → Entidade base (herança JOINED)
│   ├── Aluno.java                → Estende Usuario
│   ├── Nota.java
│   ├── Frequencia.java
│   ├── AtividadeExtra.java
│   └── Pontuacao.java
│
├── enums/
│   ├── Ranking.java              → BRONZE | PRATA | OURO | DIAMOND
│   ├── TipoAtividade.java        → Tipos de atividades extracurriculares
│   └── TipoUsuario.java          → ALUNO | PROFESSOR | ADMIN
│
├── repository/
│   ├── AlunoRepository.java
│   ├── NotaRepository.java
│   ├── FrequenciaRepository.java
│   ├── AtividadeExtraRepository.java
│   ├── PontuacaoRepository.java
│   └── UsuarioRepository.java
│
└── service/
    ├── AlunoService.java
    ├── PontuacaoService.java
    └── CustomUserDetailsService.java
```

---

## Stack Tecnológica

| Camada          | Tecnologia                      | Versão     |
|-----------------|---------------------------------|------------|
| Linguagem       | Java                            | 17         |
| Framework       | Spring Boot                     | 3.5.6      |
| Segurança       | Spring Security                 | —          |
| ORM             | Spring Data JPA + Hibernate     | —          |
| Banco de Dados  | MySQL                           | 8.0+       |
| Build           | Maven (com Maven Wrapper)       | —          |
| Utilitários     | Lombok, Spring Validation       | —          |
| Frontend        | HTML5, CSS3, JavaScript (ES6+)  | —          |
| Servidor        | Embedded Tomcat (Spring Boot)   | —          |
