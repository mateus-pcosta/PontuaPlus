# 06 — Correções de Bugs e Dados de Teste por Perfil

## Motivação

Após a expansão de perfis (ver `05-perfis-usuarios.md`), uma série de bugs críticos foram identificados: endpoints errados sendo chamados por perfis incorretos, campos ausentes em DTOs, contagens infladas por falta de filtro e usuários sem dados de teste para login. Este documento registra todas as correções.

---

## Bugs corrigidos

### 1. `GET /api/auth/me` retornava 404 para Responsavel

**Causa:** `AuthController.getCurrentUser()` só buscava na `alunoRepository`. Responsáveis não são alunos — a busca lançava `ResourceNotFoundException`.

**Correção:**
- Adicionado `GET /api/responsavel/me` em `ResponsavelAuthController`, retornando `Map<String, String>` com o campo `nome`
- `responsavel/dashboard.html` atualizado para chamar `/api/responsavel/me` em vez de `/api/auth/me`
- Guard adicionado: `(me.nome || '').split(...)` no JS do dashboard do responsável

---

### 2. `TypeError` em `eventos.html` — `me.nome.split()` sem guard

**Causa:** O `carregar()` de `eventos.html` não verificava `meRes.ok` antes de chamar `meRes.json()`. Se a chamada a `/api/auth/me` falhasse (ex: usuário sem sessão válida), `me.nome` era `undefined` e o `.split(' ')` lançava TypeError, caindo no `catch` que redirecionava inesperadamente para `/login.html`.

**Correção:**
```js
// Antes
const iniciais = me.nome.split(' ').map(n => n[0]).slice(0, 2).join('');

// Depois
if (!meRes.ok || atividadesRes.status === 401 || atividadesRes.status === 403) {
    window.location.href = '/login.html';
    return;
}
const iniciais = (me.nome || '').split(' ').map(n => n[0]).filter(Boolean).slice(0, 2).join('');
```

---

### 3. `ROLE_COORDENADOR` removido quebrava usuários existentes no banco

**Causa:** `COORDENADOR` foi removido do enum `TipoUsuario`. O JPA falha ao tentar mapear valores do banco que não existem no enum Java. Usuários com esse tipo ficavam inacessíveis.

**Correção:**
- `COORDENADOR` readicionado ao enum `TipoUsuario`
- Case adicionado no `CustomSuccessHandler`: `ROLE_COORDENADOR` → `/adm/dashboard.html`

---

### 4. `bimestreAtual` ausente em `AlunoDTO` — card sempre "—"

**Causa:** `AlunoDTO` não incluía o campo `bimestreAtual`, embora a entidade `Aluno` o tivesse. O JS de `eventos.html` usava `me.bimestreAtual ?? '—'` e sempre recebia `undefined`.

**Correção:** Campo `bimestreAtual` adicionado ao `AlunoDTO` e ao método `fromEntity()`.

---

### 5. `DiretorController.count()` sem filtro inflava "Equipe Total"

**Causa:** `colaboradorRepository.count()` contava **todos** os registros da tabela `colaboradores`, incluindo usuários DEV. O card "Equipe Total" mostrava um número maior do que o real.

**Correção:**
```java
// Antes
colaboradorRepository.count()

// Depois
colaboradorRepository.countByTipo(PROFESSOR)
+ colaboradorRepository.countByTipo(ADMINISTRADOR)
+ colaboradorRepository.countByTipo(COORDENADOR)
+ colaboradorRepository.countByTipo(DIRETOR)
```

---

### 6. `totalDiretores` calculado mas nunca exibido em `adm/dashboard.html`

**Causa:** `AdmDashboardDTO` já expunha `totalDiretores` e `AdmController` já o calculava corretamente, mas o HTML não tinha o card correspondente e o JS não populava o valor.

**Correção:** Card "Diretores" adicionado à grade de métricas; linha `document.getElementById('totalDiretores').textContent = stats.totalDiretores` adicionada ao JS.

---

## Dados de teste para todos os perfis

### Problema

O `DataInitializer` (`@Profile("dev")`) criava apenas um aluno de teste (`mateus@pontua.com`). Não havia usuários de teste para RESPONSAVEL, PROFESSOR, ADMINISTRADOR, DIRETOR ou DEV, tornando impossível testar esses dashboards sem criar usuários manualmente.

Além disso, o bloco de "early return" do DataInitializer usava `ifPresent` para re-encodar senhas — se um usuário ainda não existia no banco, ele nunca era criado nessa path.

### Solução

O `DataInitializer` foi reestruturado em três etapas que sempre executam:

1. **Re-encoding:** itera `TEST_EMAILS` e re-encoda as senhas de todos que já existem via `usuarioRepository.findByEmail()`
2. **`criarNaoAlunos()`:** verifica com `existsByEmail` antes de criar cada usuário de teste não-aluno
3. **Bloco do aluno:** continua retornando cedo se `mateus@pontua.com` já existe, evitando duplicar notas/frequências

### Usuários criados

| Perfil | Email | Senha | Redireciona para |
|---|---|---|---|
| ALUNO | mateus@pontua.com | 123456 | `/dashboard.html` |
| RESPONSAVEL | resp@pontua.com | 123456 | `/responsavel/dashboard.html` |
| PROFESSOR | prof@pontua.com | 123456 | `/professor-dashboard.html` |
| ADMINISTRADOR | adm@pontua.com | 123456 | `/adm/dashboard.html` |
| DIRETOR | diretor@pontua.com | 123456 | `/diretor/dashboard.html` |
| DEV | dev@pontua.com | 123456 | `/dev/dashboard.html` |

### Arquivos alterados

- `config/DataInitializer.java` — injeção de `ColaboradorRepository`, `ResponsavelRepository` e `UsuarioRepository`; novo método `criarNaoAlunos()`; log em bloco com todas as credenciais ao startup
- `repository/ColaboradorRepository.java` — adicionado `countByTipo(TipoUsuario)`
- `repository/UsuarioRepository.java` — adicionado `countByTipo(TipoUsuario)` e confirmado `existsByEmail`
