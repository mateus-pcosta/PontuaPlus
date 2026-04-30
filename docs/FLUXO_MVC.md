# Fluxo MVC — Pontua+

Este documento descreve como uma requisição percorre as camadas da aplicação, do navegador do usuário até o banco de dados e de volta à tela.

---

## Visão Geral das Camadas

```
Navegador (HTML/JS)
      │
      │  HTTP Request
      ▼
  Controller         ← recebe a requisição, valida entrada, delega
      │
      ▼
   Service           ← contém as regras de negócio
      │
      ▼
  Repository         ← acessa o banco via JPA/Hibernate
      │
      ▼
 Banco de Dados      ← MySQL (pontua_db)
      │
      ▼
  Repository         ← retorna entidades
      │
      ▼
   Service           ← processa e transforma
      │
      ▼
  Controller         ← monta a resposta (DTO)
      │
      │  HTTP Response (JSON)
      ▼
Navegador (HTML/JS)  ← renderiza na tela
```

---

## 1. Requisição do Usuário

O frontend é composto por páginas HTML estáticas servidas pelo próprio Spring Boot a partir de `src/main/resources/static/`. Cada página usa JavaScript puro para se comunicar com a API via `fetch`.

**Exemplo — aluno abre o dashboard:**

```javascript
// dashboard.js
async function carregarDashboard() {
    const response = await fetch('/api/dashboard');
    const data = await response.json();
    renderizarDashboard(data);
}
```

O navegador envia uma requisição `GET /api/dashboard` com o cookie de sessão do usuário autenticado. O Spring Security intercepta a requisição, valida a sessão e só então a encaminha ao controller.

---

## 2. Controller ( Mapeamento do site )

Os controllers ficam em `src/main/java/.../controller/` e são anotados com `@RestController`. Eles têm três responsabilidades:

- Receber a requisição HTTP
- Validar a entrada (formato, campos obrigatórios)
- Delegar o processamento ao Service e devolver a resposta

**Exemplo — `DashboardController.java`:**

```java
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardDTO> getDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();                          // identifica o usuário pela sessão
        return ResponseEntity.ok(dashboardService.montarDashboard(email));
    }
}
```

Controllers **não contêm regras de negócio** — apenas orquestram a chamada ao service e devolvem a resposta. Todo o processamento vive no `DashboardService`.

| Controller | Rota | Função |
|---|---|---|
| `AuthController` | `GET /api/auth/me` | Retorna dados do aluno logado |
| `DashboardController` | `GET /api/dashboard` | Dados consolidados do dashboard |
| `RegistroController` | `POST /api/registro` | Cadastro de aluno |
| `RegistroColaboradorController` | `POST /api/registro/colaborador` | Cadastro de colaborador |
| `ColaboradorAuthController` | `GET /api/colaborador/me` | Dados do colaborador logado |

---

## 3. Service

Os services ficam em `src/main/java/.../service/` e são anotados com `@Service`. É aqui que vivem as **regras de negócio** da aplicação.

**Exemplo — `PontuacaoService.java`:**

```java
@Service
@RequiredArgsConstructor
public class PontuacaoService {

    private final PontuacaoRepository pontuacaoRepository;
    private final NotaRepository notaRepository;

    @Transactional
    public Pontuacao calcularPontuacaoAluno(Aluno aluno) {
        // busca ou cria o registro de pontuação
        Pontuacao pontuacao = pontuacaoRepository.findByAluno(aluno)
                .orElse(new Pontuacao());

        int bimestre = aluno.getBimestreAtual() != null ? aluno.getBimestreAtual() : 2;

        // busca diretamente no banco somente as notas do bimestre atual
        List<Nota> notas = notaRepository.findByAlunoAndBimestre(aluno, bimestre);

        double media = notas.stream()
                .mapToDouble(Nota::getValor).average().orElse(0.0);

        pontuacao.setPontosNotas(calcularPontosPorMedia(media));   // regra de negócio aqui
        return pontuacaoRepository.save(pontuacao);
    }
}
```

> O método usa `findByAlunoAndBimestre` — uma query derivada do Spring Data JPA — em vez de carregar todas as notas e filtrar em memória. Isso reduz o volume de dados trafegados entre banco e aplicação.

| Service | Responsabilidade |
|---|---|
| `AlunoService` | CRUD de alunos, validação de duplicatas, cálculo do bimestre atual |
| `ColaboradorService` | CRUD de colaboradores, validação de duplicatas |
| `PontuacaoService` | Cálculo de pontos por bimestre e atualização do ranking |
| `DashboardService` | Montagem do DTO do dashboard (leitura, sem escrita) |
| `CustomUserDetailsService` | Carrega usuário para o Spring Security |

A anotação `@Transactional` garante que todas as operações de um método sejam confirmadas juntas ou desfeitas em caso de erro.

---

## 4. Repository

Os repositories ficam em `src/main/java/.../repository/` e estendem `JpaRepository`. Eles são a **única camada que fala diretamente com o banco de dados**. O Spring Data JPA gera a implementação automaticamente com base nos nomes dos métodos.

**Exemplo — `NotaRepository.java`:**

```java
@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByAluno(Aluno aluno);
}
```

```java
@Repository
public interface PontuacaoRepository extends JpaRepository<Pontuacao, Long> {
    Optional<Pontuacao> findByAluno(Aluno aluno);

    @Query("SELECT p FROM Pontuacao p ORDER BY p.totalPontos DESC")
    List<Pontuacao> findAllOrderByTotalPontosDesc();
}
```

| Repository | Entidade gerenciada |
|---|---|
| `UsuarioRepository` | `Usuario` |
| `AlunoRepository` | `Aluno` |
| `ColaboradorRepository` | `Colaborador` |
| `NotaRepository` | `Nota` |
| `FrequenciaRepository` | `Frequencia` |
| `AtividadeExtraRepository` | `AtividadeExtra` |
| `PontuacaoRepository` | `Pontuacao` |

---

## 5. Banco de Dados

O banco é um **MySQL 8** (`pontua_db`) com schema criado automaticamente pelo Hibernate (`ddl-auto=update`) ou pelo script `create_database.sql` (usado na inicialização Docker).

O mapeamento entre classes Java e tabelas é feito via anotações JPA nas entidades em `src/main/java/.../entity/`:

```java
@Entity
@Table(name = "notas")
public class Nota {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    private Double valor;
    private Integer bimestre;

    @PrePersist
    @PreUpdate
    private void calcularPontos() {
        // calculado automaticamente antes de salvar
        pontosConquistados = valor >= 9.0 ? 35 : valor >= 8.0 ? 32 : ...;
    }
}
```

**Estrutura das tabelas:**

```
usuarios
  └── alunos          (herança JOINED — compartilha o id)
  └── colaboradores   (herança JOINED — compartilha o id)

alunos
  ├── notas           (1 aluno → N notas)
  ├── frequencias     (1 aluno → N frequências)
  ├── atividades_extras (1 aluno → N atividades)
  └── pontuacoes      (1 aluno → 1 pontuação)
```

---

## 6. Visualização

A resposta do controller chega ao frontend como JSON. O JavaScript de cada página é responsável por pegar esse JSON e atualizar o DOM.

**Exemplo — dashboard.js renderizando a pontuação:**

```javascript
function renderizarCardsSuperiores(pontuacao) {
    document.getElementById('totalPontos').textContent = pontuacao.totalPontos;

    const notasFreq = pontuacao.pontosNotas + pontuacao.pontosFrequencia;
    document.getElementById('notasFrequencia').textContent = `${notasFreq}/50`;
    document.getElementById('rankingAtual').textContent = formatarRanking(pontuacao.ranking);
}
```

Para evitar XSS, dados vindos da API que são inseridos via `innerHTML` passam pela função `escapeHtml`:

```javascript
function escapeHtml(value) {
    const div = document.createElement('div');
    div.textContent = String(value ?? '');
    return div.innerHTML;
}

// uso seguro:
html += `<div class="materia-nome">${escapeHtml(disciplina)}</div>`;
```

**Fluxo completo de uma requisição de dashboard:**

```
1. Aluno abre dashboard.html no navegador
2. dashboard.js executa fetch('/api/dashboard')
3. Spring Security valida o cookie de sessão
4. DashboardController.getDashboard() é chamado — extrai o e-mail da sessão
5. DashboardService.montarDashboard(email) assume o controle
6. AlunoRepository executa SELECT na tabela alunos (busca por e-mail)
7. PontuacaoRepository busca a pontuação existente do aluno (sem recalcular)
8. NotaRepository, FrequenciaRepository e AtividadeExtraRepository buscam
   os registros do bimestre atual com queries filtradas no banco
9. DashboardService monta e retorna o DashboardDTO
10. DashboardController devolve ResponseEntity<DashboardDTO> com HTTP 200
11. dashboard.js recebe o JSON e atualiza a tela via escapeHtml()
```

> O dashboard é uma operação de **leitura pura** (`@Transactional(readOnly = true)`). O recálculo de pontuação só ocorre quando notas, frequências ou atividades são registradas — não a cada `GET /api/dashboard`.
