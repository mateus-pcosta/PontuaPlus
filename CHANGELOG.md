# Changelog

Todas as mudanças relevantes do projeto estão documentadas aqui.
Formato baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/).

---

## [Unreleased]

### Added
- Suporte a Docker com `Dockerfile` multi-stage e `docker-compose.yml` com MySQL + healthcheck
- Perfis de ambiente `application-dev.properties` e `application-prod.properties`
- `GlobalExceptionHandler` com respostas JSON padronizadas para erros 404, 409 e 500
- `ResourceNotFoundException` e `ConflictException` como exceções customizadas
- `DashboardService` centralizando a lógica de montagem do dashboard
- Método `calcularBimestreAtual()` em `AlunoService` baseado na data do sistema
- Métodos `registrar()` em `AlunoService` e `ColaboradorService` com validação de duplicatas
- Queries `findByAlunoAndBimestre` nos repositories de `Nota`, `Frequencia` e `AtividadeExtra`
- Testes unitários para `PontuacaoService` cobrindo todos os cenários de cálculo
- `.env.example` como template de variáveis de ambiente
- Documentação do fluxo MVC em `docs/FLUXO_MVC.md`
- `ConflictException` com retorno HTTP 409 para duplicatas de e-mail e matrícula

### Changed
- Credenciais do banco movidas de `application.properties` para variáveis de ambiente (`${DB_PASSWORD}`)
- `SecurityConfig` agora usa `CustomSuccessHandler` para redirecionar por papel do usuário
- `SecurityConfig` com CORS configurável via propriedade `app.cors.allowed-origins`
- `TipoUsuario` expandido com `COORDENADOR` e `DIRETOR`
- `ColaboradorAuthController` passou a retornar `ColaboradorDTO` em vez da entidade bruta
- `DataInitializer` restrito ao perfil `dev` com `@Profile("dev")`
- `DataInitializer` substituiu `System.out.println` por logger SLF4J
- `PontuacaoService` usa queries filtradas por bimestre no banco em vez de filtros em memória
- `RegistroController` e `RegistroColaboradorController` delegam toda lógica ao service
- `DashboardController` delegado ao `DashboardService` — sem acesso direto a repositories
- Anotações Lombok nas entidades JPA trocadas de `@Data` para `@Getter`/`@Setter`/`@ToString`/`@EqualsAndHashCode` com configuração granular
- `create_database.sql` sincronizado com as entidades (colunas `cpf`, `bimestre_atual`, `bimestre`, tabela `colaboradores`)
- `create_database.sql` montado como init script no Docker via `/docker-entrypoint-initdb.d/`
- Dados de teste no SQL corrigidos com valores realistas e tipos de atividade válidos

### Fixed
- XSS em `dashboard.js` — dados da API escapados com `escapeHtml()` antes de inserção via `innerHTML`
- Chamada duplicada à API em `colaborador-perfil.js`
- `console.error` removido dos arquivos JS para não expor stack traces em produção
- Hash BCrypt placeholder no SQL substituído por hash real
- Pontuação de teste no SQL corrigida (era `total_pontos = 303`, impossível; corrigido para `76`)
