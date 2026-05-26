# Changelog

Todas as mudanças relevantes do projeto estão documentadas aqui.
Formato baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/).

---

## [Unreleased]

### Added
- Tela de Ranking (`/ranking.html`) com cards por tier (Diamond, Ouro, Prata, Bronze) e lista de alunos expansível por clique
- Endpoint `GET /api/ranking` com controle de acesso por tier — alunos só visualizam dados dos tiers iguais ou superiores ao seu nível
- `RankingController` injetando `Authentication` como parâmetro de método em vez de `SecurityContextHolder`
- `RankingDTO` com `TierDTO` e `AlunoRankingDTO` — expõe apenas `nome`, `totalPontos`, `posicaoRanking` e `euMesmo`, sem dados sensíveis
- Método `montarRanking()` em `PontuacaoService` com lógica de acessibilidade por tier
- Queries `findByRankingOrderByTotalPontosDesc` e `countByRanking` em `PontuacaoRepository`
- Barra de navegação global em todas as páginas autenticadas com links para Desempenho, Ranking e itens futuros (Recompensas, Eventos, Acessibilidade)
- 16 alunos fictícios distribuídos entre os 4 tiers para demonstração do ranking
- Notas do 3º bimestre (agosto–setembro) e 4º bimestre (outubro–dezembro) para todos os 17 alunos
- Frequências mensais de agosto a dezembro para todos os alunos
- `run.ps1` para facilitar a execução local no Windows com carregamento automático do `.env`
- Logos da marca integradas em todas as páginas — `logopreto.png` em headers brancos e `pontuamais_logo.png` no hero da `index.html`
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
- `DashboardService` agora retorna todos os registros de notas e frequências sem filtro de bimestre, permitindo exibir histórico completo nos gráficos
- Gráfico de evolução das notas em `dashboard.js` reorganizado para exibir dados agrupados por bimestre em ordem cronológica (bim 1 → bim 4)
- Gráfico de frequência mensal reordenado cronologicamente por `(ano * 12 + mes)`
- `bimestre_atual` de todos os alunos atualizado para `3` no banco de dados de exemplo
- `PontuacaoService.atualizarRankings()` migrado de `save()` em loop para `saveAll()` após o loop
- Cards do dashboard (`metric-card`) migrados de fundo gradiente para fundo branco com valores em azul
- Animação shimmer das barras de progresso alterada para tocar apenas uma vez ao carregar a página
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
- Mensagem de erro no login não exibia ao retornar `?error` — `urlParams.get('error')` retorna `""` (falsy) para parâmetros sem valor; corrigido para `urlParams.has('error')`, mesmo fix aplicado ao `?logout`
- `DataInitializer` agora re-codifica a senha do usuário de teste ao iniciar em modo `dev`, eliminando incompatibilidade entre hash BCrypt do SQL e a senha `123456`
- Parâmetro `aluno` renomeado para `colaborador` em `renderizarPerfil` de `colaborador-perfil.js` — o endpoint retorna dados de colaborador, não de aluno
- Ícone de taça (ranking) em `dashboard.html` e `ranking.html` estava incompleto — faltavam os dois `path` do pedestal do troféu
- XSS em `dashboard.js` — dados da API escapados com `escapeHtml()` antes de inserção via `innerHTML`
- Chamada duplicada à API em `colaborador-perfil.js`
- `console.error` removido dos arquivos JS para não expor stack traces em produção
- Hash BCrypt placeholder no SQL substituído por hash real
- Pontuação de teste no SQL corrigida (era `total_pontos = 303`, impossível; corrigido para `76`)
