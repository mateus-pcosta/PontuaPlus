# 07 — Painel do Professor, Redirects DEV e Enriquecimento do Painel DEV

## Motivação

Três problemas foram identificados e corrigidos nesta iteração:

1. `professor-dashboard.html` exibia dados 100% hardcoded de template de faculdade
2. Alguns dashboards redirecionavam para login quando acessados via painel DEV
3. O painel DEV tinha poucos dados — apenas contagens de usuários por perfil

---

## Problema 1 — professor-dashboard.html com dados de faculdade

### Causa

A página foi criada a partir de um template genérico com "Logística 1º Período", "RH 3º Período". Não havia endpoint de backend — `ProfessorController` não existia.

### Solução

**`ProfessorTurmaDTO.java`** — novo record:
```java
record ProfessorTurmaDTO(String turma, String serie, long totalAlunos) {}
```

**`AlunoRepository.java`** — nova query JPQL:
```java
@Query("SELECT a.turma, a.serie, COUNT(a) FROM Aluno a GROUP BY a.turma, a.serie ORDER BY a.serie, a.turma")
List<Object[]> findTurmasComContagem();
```

**`ProfessorController.java`** — novo controller:
```java
@GetMapping("/turmas")
@PreAuthorize("hasRole('PROFESSOR') or hasRole('DEV')")
public ResponseEntity<List<ProfessorTurmaDTO>> getTurmas() { ... }
```

**`professor-dashboard.html`** reescrito com:
- `async function carregar()` + `Promise.all` para `/api/colaborador/me` e `/api/professor/turmas`
- Check 401/403 → redirect para login
- Tabela com colunas: Turma | Série | Alunos | Ações
- Métricas calculadas da API (`totalTurmas`, `totalAlunos`)

---

## Problema 2 — Redirects indevidos para login via painel DEV

### Causa raiz

| Dashboard destino | Comportamento para DEV |
|---|---|
| `/professor-dashboard.html` | Funciona — APIs aceitam `hasRole('DEV')` |
| `/adm/dashboard.html` | Funciona — DEV em `colaboradores` |
| `/diretor/dashboard.html` | Funciona — mesmo caso |
| `/dashboard.html` (aluno) | 404 → catch mostrava `alert()` browser (não redirecionava) |
| `/responsavel/dashboard.html` | 404 → `res.json()` num body de erro → TypeError → **catch cego redirecionava** |

### Correções

**`responsavel/dashboard.html`** — adicionado check antes de `res.json()`:
```js
if (!res.ok) {
    // mostra mensagem inline, não redireciona
    document.getElementById('listaVazia').textContent = 'Dados indisponíveis para este perfil.';
    return;
}
```

**`dashboard.js`** — substituído `alert()` por mensagem inline no spinner:
```js
// antes
alert('Erro ao carregar dados do dashboard');

// depois
loading.innerHTML = '<p style="...">Não foi possível carregar os dados deste perfil.</p>';
```

**`dev/dashboard.html`** — botões "Acessar Dashboards" restaurados. Com as correções acima, nenhum dashboard redireciona para login ao ser acessado pelo DEV.

---

## Problema 3 — Painel DEV com poucos dados

### Solução

**`DevStatsDTO.java`** — 4 campos adicionados:
```java
private long totalNotas;
private long totalFrequencias;
private long totalAtividades;
private long totalTurmas;
```

**`DevController.java`** — injetados `NotaRepository`, `FrequenciaRepository`, `AtividadeExtraRepository`; campos calculados no `getStats()`.

**`dev/dashboard.html`** — segunda linha de métricas:
```
[ Notas lançadas ]  [ Frequências ]  [ Atividades extras ]  [ Turmas cadastradas ]
```

---

## Arquivos alterados

| Arquivo | Ação |
|---|---|
| `controller/ProfessorController.java` | Criado |
| `dto/ProfessorTurmaDTO.java` | Criado |
| `repository/AlunoRepository.java` | Adicionada `findTurmasComContagem()` |
| `static/professor-dashboard.html` | Reescrito (dados reais, K-12) |
| `static/responsavel/dashboard.html` | Check `!res.ok` antes de `res.json()` |
| `static/js/dashboard.js` | `alert()` → mensagem inline |
| `static/dev/dashboard.html` | Botões restaurados + segunda linha de métricas |
| `dto/DevStatsDTO.java` | 4 campos novos |
| `controller/DevController.java` | 3 repos injetados, campos calculados |
