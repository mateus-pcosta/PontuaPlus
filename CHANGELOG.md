# Changelog

Todas as mudanças relevantes do projeto estão documentadas aqui.
Formato baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/).

---

## [Unreleased]

### Added
- Tela de Recompensas (`/recompensas.html`) com card de status, seções por tier e modal "em breve" para resgate
- Endpoint `GET /api/recompensas` com catálogo de recompensas por tier e status atual do aluno
- Endpoint `GET /api/recompensas/emblemas` retornando apenas os emblemas do aluno autenticado
- Entidade `Recompensa` (catálogo estático) e `EmblemaDigital` (1 emblema por aluno por bimestre)
- `RecompensaService.montarRecompensas()` com lógica de desbloqueio: somente o tier atual do aluno permite resgate
- Criação automática de emblema em `PontuacaoService.calcularPontuacaoAluno()` ao final de cada cálculo
- Seção "Emblemas Digitais" na página de perfil, carregada via `GET /api/recompensas/emblemas`
- Seed de 9 recompensas fictícias (parceiros locais de Teresina/Timon) e 17 emblemas no `create_database.sql`
- Link "Recompensas" habilitado na navegação global (dashboard, ranking e perfil)
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
- `DataInitializer` estendido com usuários de teste para todos os perfis: RESPONSAVEL (`resp@pontua.com`), PROFESSOR (`prof@pontua.com`), ADMINISTRADOR (`adm@pontua.com`), DIRETOR (`diretor@pontua.com`) e DEV (`dev@pontua.com`) — todos com senha `123456`; criação via método `criarNaoAlunos()` com guard `existsByEmail` para evitar duplicatas em reinicializações
- `DataInitializer` agora re-encoda senhas de **todos** os usuários de teste existentes no startup (não apenas `mateus@pontua.com`) usando `usuarioRepository.findByEmail()` na superclasse `Usuario`
- `SecurityConfig` anotado com `@EnableMethodSecurity` para habilitar `@PreAuthorize` nos controllers; `/responsavel-registro.html` adicionado a `permitAll`
- `ColaboradorRepository` — adicionado `countByTipo(TipoUsuario)` (necessário para `DiretorController` e `AdmController`)
- `UsuarioRepository` — adicionado `countByTipo(TipoUsuario)` para contagens por perfil
- Link "Eventos" habilitado na barra de navegação global de `dashboard.html`, `ranking.html`, `recompensas.html` e `perfil.html` — era `<span class="nav-link disabled">` e passou a `<a href="/eventos.html">`
- Formulário de registro de colaborador (`colaborador-registro.html`) atualizado: opções de cargo agora refletem os tipos ativos do sistema — adicionados `DEV` e `ADMINISTRADOR`, removido `COORDENADOR` (usuários existentes com esse tipo continuam funcionando via enum; novos registros devem usar `ADMINISTRADOR`)
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
- `GET /api/auth/me` retornava 404 para usuários do tipo Responsavel — adicionado endpoint `GET /api/responsavel/me` em `ResponsavelAuthController`; `responsavel/dashboard.html` atualizado para chamar o endpoint correto
- `TypeError` em `eventos.html` ao chamar `me.nome.split()` sem guard — adicionada verificação de `meRes.ok` e uso de `(me.nome || '')` antes do split para evitar crash silencioso seguido de redirect inesperado para login
- `ROLE_COORDENADOR` removido do `TipoUsuario` quebrava usuários com esse tipo no banco de dados (mapeamento JPA falha) — `COORDENADOR` readicionado ao enum e tratado no `CustomSuccessHandler` com redirect para `/adm/dashboard.html`
- `bimestreAtual` ausente no `AlunoDTO` — campo adicionado ao DTO e incluído no `fromEntity()`, corrigindo card "Bimestre Atual" em `eventos.html` que sempre exibia "—"
- `DiretorController` usava `colaboradorRepository.count()` sem filtro de tipo, inflando o card "Equipe Total" com usuários DEV e outros — substituído por soma de `countByTipo(PROFESSOR)`, `countByTipo(ADMINISTRADOR)` e `countByTipo(DIRETOR)`
- `totalDiretores` era calculado em `AdmDashboardDTO` e retornado por `AdmController` mas nunca exibido em `adm/dashboard.html` — card "Diretores" adicionado à grade de métricas
- Mensagem de erro no login não exibia ao retornar `?error` — `urlParams.get('error')` retorna `""` (falsy) para parâmetros sem valor; corrigido para `urlParams.has('error')`, mesmo fix aplicado ao `?logout`
- `DataInitializer` agora re-codifica a senha do usuário de teste ao iniciar em modo `dev`, eliminando incompatibilidade entre hash BCrypt do SQL e a senha `123456`
- Parâmetro `aluno` renomeado para `colaborador` em `renderizarPerfil` de `colaborador-perfil.js` — o endpoint retorna dados de colaborador, não de aluno
- Ícone de taça (ranking) em `dashboard.html` e `ranking.html` estava incompleto — faltavam os dois `path` do pedestal do troféu
- Barra de progresso na tela de Recompensas piscava ao carregar — causado por `transition: width 1s` e animação shimmer do `.progress-fill`; resolvido com classe `.progress-fill-static` que desativa transição e animação apenas nessa barra
- XSS em `dashboard.js` — dados da API escapados com `escapeHtml()` antes de inserção via `innerHTML`
- Chamada duplicada à API em `colaborador-perfil.js`
- `console.error` removido dos arquivos JS para não expor stack traces em produção
- Hash BCrypt placeholder no SQL substituído por hash real
- Pontuação de teste no SQL corrigida (era `total_pontos = 303`, impossível; corrigido para `76`)
