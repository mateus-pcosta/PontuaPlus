# 08 — Aba de Acessibilidade

## Motivação

O link "Acessibilidade" já existia como `<span class="nav-link disabled">` em todas as dashboards prontas, sinalizando uma funcionalidade planejada e nunca implementada. O pedido foi construir essa aba de verdade, com controles reais (não um checklist genérico), reaproveitando só a paleta/tipografia já definidas em `css/styles.css` — sem introduzir cores ou fontes novas — e com uma camada de backend desde já, pensada para evoluir depois (sincronizar preferências entre dispositivos, não só `localStorage`).

---

## Skill do projeto

Criada `.claude/skills/accessibility-tab/SKILL.md`: documenta o padrão a seguir sempre que essa aba (ou um novo controle de acessibilidade) for tocada — quais tokens de CSS usar, estrutura de página, lista de controles esperados e um checklist WCAG 2.1 AA. Baseada nos princípios do skill público `frontend-design` (do repositório `anthropics/skills`), mas travando a etapa de paleta/tipografia nos tokens que o projeto já tem, já que aqui o design system é fixo.

---

## Backend — preferências por usuário

Antes só existia a opção de guardar tudo em `localStorage`. Para permitir sincronizar entre dispositivos no futuro, as preferências são persistidas por usuário desde a primeira versão:

**`enums/TamanhoFonte.java`** — novo enum: `PEQUENA`, `MEDIA`, `GRANDE`.

**`entity/AcessibilidadePreferencia.java`** — nova entidade, `@OneToOne` com `Usuario` (FK única em `usuario_id`), campos booleanos `altoContraste`, `reduzMovimento`, `sublinharLinks`, `libras`, `leituraVoz`, mais `tamanhoFonte`. Tabela `acessibilidade_preferencias` criada automaticamente via `ddl-auto=update`.

**`repository/AcessibilidadePreferenciaRepository.java`** — `findByUsuario(Usuario)`.

**`dto/AcessibilidadePreferenciaDTO.java`** — DTO plano com `fromEntity()`.

**`service/AcessibilidadePreferenciaService.java`** — `buscar(email)` e `salvar(email, dto)`, ambos com padrão get-or-create (mesmo padrão já usado em `DashboardService` para `Pontuacao`). Campos booleanos nulos vindos do cliente são tratados como `false` (`Boolean.TRUE.equals(...)`), já que é fronteira de entrada externa.

**`controller/AcessibilidadeController.java`** — `GET` e `PUT /api/acessibilidade`, sem `@PreAuthorize` (qualquer perfil autenticado tem suas próprias preferências, igual ao `DashboardController`).

**`config/SecurityConfig.java`** — `/acessibilidade.html` adicionado à lista de rotas estáticas `permitAll()` (mesmo padrão de `dashboard.html`, `ranking.html` etc. — o shell HTML é público, os dados vêm autenticados via `/api/**`).

---

## Frontend

**`acessibilidade.html`** — nova página, mesma estrutura de header/nav/`.card`/`.card-glass` das demais. Controles:

| Controle | Efeito |
|---|---|
| Tamanho da fonte (A- / A / A+) | Classe em `<html>`, escala global via `font-size` |
| Alto contraste | Classe em `<body>`, troca tokens de cinza por pares com contraste AA e remove `backdrop-filter` |
| Reduzir movimento | Classe em `<body>`, zera toda `animation`/`transition` |
| Sublinhar links | Classe em `<body>`, força `text-decoration: underline` em todo link |
| Tradutor de Libras (VLibras) | Injeta o widget oficial `vlibras.gov.br` só quando ativado |
| Leitura em voz alta | Web Speech API — lê o elemento focado (teclado) ou o texto selecionado |

**`js/acessibilidade.js`** — script único incluído logo após `<body>` em todas as páginas autenticadas. Aplica as preferências do `localStorage` imediatamente (evita flash de conteúdo sem estilo), depois busca `GET /api/acessibilidade` e reaplica com os dados do servidor. Cada alteração feita em `acessibilidade.html` chama `PontuaA11y.definir(chave, valor)`, que atualiza `localStorage`, reaplica na hora e dispara `PUT /api/acessibilidade` em background. Nenhuma outra página duplica essa lógica.

**`css/styles.css`** — nova seção `ACESSIBILIDADE`: classes `a11y-*`, switch acessível (`<input type="checkbox">` real por baixo, nunca `display:none`), reforço de `:focus-visible` em todo elemento interativo.

**Navegação** — nas 7 páginas que já tinham o placeholder (`dashboard.html`, `ranking.html`, `recompensas.html`, `eventos.html`, `perfil.html`, `professor-dashboard.html`, `colaborador-perfil.html`), o `<span class="nav-link disabled">Acessibilidade</span>` virou `<a href="/acessibilidade.html">`. Os painéis `adm/`, `diretor/`, `responsavel/` e `dev/` ficaram de fora por ainda serem esqueleto (mesmo critério dos outros itens de nav desabilitados neles).

---

## Bug encontrado e corrigido durante o teste

`.a11y-switch` não tinha `position: relative`. O `<input>` escondido (necessário pro clique/teclado funcionarem) ficava posicionado relativo ao ancestral posicionado mais próximo — que por acaso era o `.header` (`position: sticky`) — em vez de ficar sobre o próprio switch. O clique do mouse continuava funcionando (clique em `<label>` sempre repassa pro input associado, não importa onde ele esteja na tela), mas o anel de foco visível ao navegar por teclado aparecia fora do lugar. Corrigido com um `position: relative` no `.a11y-switch`.

Validado com um teste ponta a ponta via Playwright: login → dashboard → aba de Acessibilidade → cada toggle → reload completo da página → navegação pra outra página — as preferências continuaram aplicadas em todas as etapas, confirmando que vêm do backend e não só do `localStorage`.

---

## Removido a pedido do usuário

O skip link (`<a href="#main-content" class="skip-link">Pular para o conteúdo</a>`, primeiro elemento focável de cada página, usado pra pular a navegação e ir direto pro conteúdo) foi percebido como um botão confuso "meio escondido" no topo da tela. Removido de todas as 8 páginas (as 7 acima + `acessibilidade.html`) junto com a classe `.skip-link` no CSS.

---

## Arquivos criados

| Arquivo | Descrição |
|---|---|
| `.claude/skills/accessibility-tab/SKILL.md` | Skill do projeto para a aba de Acessibilidade |
| `enums/TamanhoFonte.java` | Enum `PEQUENA/MEDIA/GRANDE` |
| `entity/AcessibilidadePreferencia.java` | Entidade `@OneToOne` com `Usuario` |
| `repository/AcessibilidadePreferenciaRepository.java` | `findByUsuario()` |
| `dto/AcessibilidadePreferenciaDTO.java` | DTO plano |
| `service/AcessibilidadePreferenciaService.java` | `buscar()`/`salvar()`, get-or-create |
| `controller/AcessibilidadeController.java` | `GET`/`PUT /api/acessibilidade` |
| `static/acessibilidade.html` | Página da aba |
| `static/js/acessibilidade.js` | Aplicação + sincronização das preferências |

## Arquivos alterados

| Arquivo | Ação |
|---|---|
| `config/SecurityConfig.java` | `/acessibilidade.html` em `permitAll()` |
| `static/css/styles.css` | Seção `ACESSIBILIDADE` adicionada; `.skip-link` removida |
| `static/dashboard.html`, `ranking.html`, `recompensas.html`, `eventos.html`, `perfil.html`, `professor-dashboard.html`, `colaborador-perfil.html` | Nav real + include de `acessibilidade.js`; skip link removido |
