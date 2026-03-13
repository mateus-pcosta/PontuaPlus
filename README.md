# Pontua+

> Plataforma de gamificação educacional que transforma desempenho escolar em pontuação, rankings e recompensas.

---

## Sobre o Projeto

O **Pontua+** é uma plataforma digital de **gamificação educacional** que transforma o desempenho escolar em um sistema de pontuação e recompensas. A plataforma integra dados acadêmicos, como **notas e frequência**, com a participação em **atividades extracurriculares**, permitindo que os alunos acumulem pontos e avancem em rankings (Bronze, Silver, Gold e Diamond).

O objetivo do sistema é **reduzir a evasão escolar**, tornando o ambiente educacional mais motivador, interativo e participativo. Ao reconhecer não apenas o desempenho em provas, mas também o envolvimento em eventos, cursos, projetos voluntários e outras iniciativas, o Pontua+ estimula os alunos a se manterem engajados ao longo de toda a jornada escolar.

Além disso, a plataforma também busca **preparar os estudantes para o "depois" da escola**. Ao incentivar a participação em atividades extracurriculares, o sistema promove o desenvolvimento de **habilidades importantes para o mercado de trabalho**, como colaboração, iniciativa, responsabilidade e participação social. Dessa forma, o aluno não apenas melhora seu desempenho acadêmico, mas também se desenvolve como pessoa e como futuro profissional.

O Pontua+ também promove **competição saudável e colaboração entre alunos**, por meio de rankings e atividades coletivas. Conforme os estudantes evoluem em pontuação, podem **desbloquear recompensas e benefícios oferecidos por empresas parceiras**, tornando o progresso visível e valorizado.

Além disso, a plataforma inclui recursos de **acessibilidade e inclusão**, oferecendo ferramentas para alunos com diferentes necessidades e incentivando atitudes solidárias dentro da comunidade escolar. Dessa forma, o Pontua+ busca construir uma experiência educacional mais **motivadora, inclusiva e preparada para o futuro dos estudantes**.

---

## Sistema de Pontuação

A pontuação de cada aluno é calculada com base em **3 pilares**, totalizando até **100 pontos por bimestre**:

### 1. Notas (até 35 pontos)

| Média das Notas | Pontos |
|-----------------|--------|
| 9.0 – 10.0      | 35     |
| 8.0 – 8.9       | 32     |
| 7.0 – 7.9       | 25     |
| 6.0 – 6.9       | 20     |
| 5.0 – 5.9       | 15     |
| Abaixo de 5.0   | 0      |

### 2. Frequência (até 15 pontos)

| Frequência Média | Pontos |
|------------------|--------|
| 95% – 100%       | 15     |
| 90% – 94%        | 13     |
| 85% – 89%        | 11     |
| 80% – 84%        | 9      |
| 75% – 79%        | 7      |
| Abaixo de 75%    | 0      |

### 3. Atividades Extracurriculares (até 50 pontos)

| Categoria              | Atividade                     | Pontos |
|------------------------|-------------------------------|--------|
| Liderança              | Líder de Turma                | 10     |
| Liderança              | Sistema de Companheiros       | 10     |
| Olimpíadas             | OBMEP / Olimp. de Matemática  | 5      |
| Clubes                 | Robótica / Debate / Ciências  | 10     |
| Criação de Conteúdo    | Áudio / Vídeo                 | 15     |
| Criação de Conteúdo    | Conteúdo Escrito              | 10     |
| Voluntariado           | Qualquer iniciativa           | 10     |

---

## Rankings

| Ranking         | Faixa de Pontos | Nível        |
|-----------------|-----------------|--------------|
| 🥉 Bronze       | 0 – 25          | Iniciante    |
| 🥈 Prata        | 26 – 64         | Intermediário |
| 🥇 Ouro         | 65 – 80         | Avançado     |
| 💎 Diamond      | 81 – 100        | Elite        |

---

## Tecnologias

| Camada       | Tecnologia                        |
|--------------|-----------------------------------|
| Backend      | Java 17 + Spring Boot 3.5.6       |
| Segurança    | Spring Security                   |
| Persistência | Spring Data JPA + Hibernate       |
| Banco de Dados | MySQL 8.0+                      |
| Build        | Maven (Maven Wrapper incluso)     |
| Utilitários  | Lombok, Spring Validation         |
| Frontend     | HTML, CSS, JavaScript (estático)  |

---

## Pré-requisitos

- [Java 17+](https://adoptium.net/)
- [MySQL 8.0+](https://dev.mysql.com/downloads/)
- Maven (ou use o `mvnw` incluso no projeto)

---

## Como Executar

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/pontua-plus.git
cd pontua-plus
```

### 2. Configure o banco de dados

Acesse o MySQL e crie o banco:

```sql
CREATE DATABASE pontua_db;
```

Verifique as credenciais em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pontua_db
spring.datasource.username=root
spring.datasource.password=root
```

### 3. Execute o projeto

**Windows:**
```bash
.\mvnw.cmd spring-boot:run
```

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

### 4. Acesse o sistema

Abra no navegador: [http://localhost:8080](http://localhost:8080)

**Credenciais de teste:**
- E-mail: `mateus@pontua.com`
- Senha: `123456`

> O sistema carrega automaticamente dados de exemplo ao iniciar (aluno Mateus Pessoa Costa com notas, frequências e atividades do 2º bimestre).

---

## Estrutura do Projeto

```
src/
└── main/
    ├── java/com/pontuaplus/pontua_plus/
    │   ├── config/         # Configurações de segurança e dados iniciais
    │   ├── controller/     # Endpoints REST (Auth, Dashboard, Registro)
    │   ├── dto/            # Objetos de transferência de dados
    │   ├── entity/         # Entidades JPA (Aluno, Nota, Frequencia, etc.)
    │   ├── enums/          # Enumerações (Ranking, TipoAtividade, TipoUsuario)
    │   ├── repository/     # Repositórios Spring Data JPA
    │   └── service/        # Regras de negócio (PontuacaoService, AlunoService)
    └── resources/
        ├── static/         # Frontend (HTML, CSS, JS)
        └── application.properties
```

---

## Endpoints da API

| Método | Endpoint         | Descrição                          | Autenticação |
|--------|------------------|------------------------------------|--------------|
| POST   | `/auth/login`    | Autenticar aluno                   | Não          |
| POST   | `/registro`      | Cadastrar novo aluno               | Não          |
| GET    | `/dashboard`     | Dados do aluno autenticado         | Sim          |

---

## Documentação Adicional

- [Guia Rápido de Execução](GUIA_RAPIDO.md)
- [Sistema de Pontuação Detalhado](NOVO_SISTEMA_PONTUACAO.md)

---

## Roadmap

- [ ] Painel do professor com visão geral da turma
- [ ] Histórico de pontuação por bimestre
- [ ] Sistema de recompensas com empresas parceiras
- [ ] Notificações e metas personalizadas
- [ ] API aberta para integração com sistemas escolares
- [ ] Recursos de acessibilidade aprimorados
- [ ] Aplicativo mobile
