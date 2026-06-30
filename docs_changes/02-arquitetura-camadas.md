# Refatoração da Arquitetura em Camadas

## O que foi mudado

Cinco problemas arquiteturais foram corrigidos em um único conjunto de mudanças:

1. Controllers acessavam repositories diretamente (bypass do service)
2. Dashboard recalculava e escrevia no banco a cada requisição de leitura
3. Filtros por bimestre feitos em Java em vez de no banco
4. `bimestreAtual` hardcoded para o valor `2`
5. `DataInitializer` rodava em todos os ambientes, incluindo produção

---

## Problema 1 — Controllers acessando repositories diretamente

### Antes

`RegistroController` injetava `AlunoRepository` e `PasswordEncoder` e fazia tudo:

```java
// RegistroController.java — ANTES
private final AlunoRepository alunoRepository;   // repository no controller
private final PasswordEncoder passwordEncoder;   // infraestrutura no controller

// lógica de negócio no controller:
if (alunoRepository.findByEmail(dto.getEmail()).isPresent()) { ... }
aluno.setSenha(passwordEncoder.encode(dto.getSenha()));
aluno.setBimestreAtual(2);
alunoRepository.save(aluno);
```

`DashboardController` injetava 3 repositories:

```java
private final NotaRepository notaRepository;
private final FrequenciaRepository frequenciaRepository;
private final AtividadeExtraRepository atividadeExtraRepository;
```

### Por que isso é um problema

A arquitetura em camadas tem um propósito claro:

```
Controller → Service → Repository → Banco
```

Cada camada tem uma responsabilidade:
- **Controller**: receber HTTP, validar formato, retornar resposta
- **Service**: aplicar regras de negócio, orquestrar operações
- **Repository**: falar com o banco de dados

Quando o controller acessa o repository diretamente, você perde:

**Testabilidade**: Para testar o controller, você precisa de um banco real ou mockar o repository. Se a lógica estivesse no service, bastaria mockar o service (muito mais simples).

**Reusabilidade**: Se outro controller ou outro service precisar registrar um aluno, terá que duplicar toda a lógica de validação e criação.

**Manutenibilidade**: Se a regra de negócio mudar (ex: exigir confirmação de e-mail), você tem que caçar onde ela está — no controller? no service? em ambos?

### Depois

`RegistroController` agora tem 12 linhas e zero lógica de negócio:

```java
// RegistroController.java — DEPOIS
private final AlunoService alunoService;

@PostMapping
public ResponseEntity<Map<String, String>> registrarAluno(@Valid @RequestBody RegistroAlunoDTO dto) {
    alunoService.registrar(dto);
    return ResponseEntity.ok(Map.of("success", "Cadastro realizado com sucesso!", ...));
}
```

Toda a lógica de negócio foi para `AlunoService.registrar()`:

```java
// AlunoService.java — lógica centralizada no service
@Transactional
public Aluno registrar(RegistroAlunoDTO dto) {
    if (alunoRepository.findByEmail(dto.getEmail()).isPresent()) {
        throw new ConflictException("E-mail já cadastrado");
    }
    if (alunoRepository.existsByMatricula(dto.getMatricula())) {
        throw new ConflictException("Matrícula já cadastrada");
    }
    // ... criar e salvar aluno
}
```

E o `DashboardController` foi delegado ao `DashboardService`:

```java
// DashboardController.java — DEPOIS
private final DashboardService dashboardService;

@GetMapping
public ResponseEntity<DashboardDTO> getDashboard() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return ResponseEntity.ok(dashboardService.montarDashboard(auth.getName()));
}
```

---

## Problema 2 — Dashboard escrevia no banco a cada leitura

### Antes

```java
// DashboardController.java — ANTES
@GetMapping
public ResponseEntity<DashboardDTO> getDashboard() {
    Pontuacao pontuacao = pontuacaoService.calcularPontuacaoAluno(aluno); // ESCRITA no banco
    pontuacaoService.atualizarRankings();                                  // ESCRITA para todos os alunos
    // ...
}
```

Cada vez que qualquer aluno abria o dashboard:
1. O sistema recalculava a pontuação do aluno e salvava no banco
2. O sistema atualizava a posição de ranking de **todos** os alunos no banco

Com 100 alunos, um simples `GET /api/dashboard` gerava 101 operações de escrita no banco.

### Por que isso é um problema

**Violação do princípio de que leitura não deve causar efeitos colaterais**: Um `GET` HTTP deve ser idempotente — chamar 10 vezes deve retornar o mesmo resultado sem alterar o estado do sistema.

**Escalabilidade**: O custo de `atualizarRankings()` cresce linearmente com o número de alunos. Com 1.000 alunos, cada visualização de dashboard atualiza 1.000 registros.

**Corretude**: A pontuação de um aluno só muda quando suas notas, frequências ou atividades mudam. Recalcular a cada visualização é redundante.

### Depois

`DashboardService` só lê dados existentes:

```java
// DashboardService.java — DEPOIS (apenas leitura)
@Transactional(readOnly = true)
public DashboardDTO montarDashboard(String email) {
    Aluno aluno = alunoService.buscarPorEmail(email)...;
    
    // Apenas lê a pontuação existente, cria vazia se nunca foi calculada
    Pontuacao pontuacao = pontuacaoRepository.findByAluno(aluno)
            .orElseGet(() -> {
                Pontuacao nova = new Pontuacao();
                nova.setAluno(aluno);
                return pontuacaoRepository.save(nova); // só cria uma vez
            });

    // Apenas busca dados — sem calcular, sem atualizar
    List<Nota> notas = notaRepository.findByAlunoAndBimestre(aluno, bimestre);
    // ...
    return DashboardDTO.fromEntities(...);
}
```

O `@Transactional(readOnly = true)` informa ao Hibernate que essa transação não vai escrever — ele pode fazer otimizações internas e o banco pode usar réplicas de leitura se existirem.

O recálculo (`calcularPontuacaoAluno` + `atualizarRankings`) ficou no `PontuacaoService` e deve ser chamado pelos endpoints que **alteram** notas, frequências ou atividades — que serão implementados futuramente.

---

## Problema 3 — Filtros por bimestre em Java em vez de no banco

### Antes

```java
// PontuacaoService.java — ANTES
List<Nota> notasBimestre = notaRepository.findByAluno(aluno)  // busca TUDO do aluno
        .stream()
        .filter(n -> n.getBimestre().equals(bimestreAtual))    // filtra em memória no Java
        .toList();
```

### Por que isso é um problema

Imagine um aluno com 4 anos de histórico: 4 bimestres × 4 anos × 5 disciplinas = 80 notas. O código carregava as 80 do banco e descartava 75 delas em memória.

Banco de dados são muito mais eficientes em filtrar dados do que código Java — é exatamente para isso que existem índices e a cláusula `WHERE`.

### Depois

Novos métodos foram adicionados nos repositories usando a convenção de nomenclatura do Spring Data JPA:

```java
// NotaRepository.java
List<Nota> findByAlunoAndBimestre(Aluno aluno, Integer bimestre);

// FrequenciaRepository.java
List<Frequencia> findByAlunoAndBimestre(Aluno aluno, Integer bimestre);

// AtividadeExtraRepository.java
List<AtividadeExtra> findByAlunoAndBimestre(Aluno aluno, Integer bimestre);
```

O Spring Data JPA lê o nome do método e gera automaticamente o SQL equivalente:

```sql
SELECT * FROM notas WHERE aluno_id = ? AND bimestre = ?
```

O uso no service ficou mais simples e correto:

```java
// PontuacaoService.java — DEPOIS
List<Nota> notas = notaRepository.findByAlunoAndBimestre(aluno, bimestre);
```

### Como o Spring Data JPA resolve nomes de métodos

É uma convenção: `findBy` + `NomeDoCampo` + `And` + `NomeDoCampo`. O Spring interpreta isso em tempo de inicialização e gera o JPQL:

```
findByAlunoAndBimestre → SELECT n FROM Nota n WHERE n.aluno = ?1 AND n.bimestre = ?2
```

Se o campo não existir na entidade, a aplicação falha na inicialização — o que é bom, porque você descobre o erro cedo.

---

## Problema 4 — `bimestreAtual` hardcoded para 2

### Antes

Em dois lugares diferentes o valor `2` aparecia hardcoded:

```java
// RegistroController.java
aluno.setBimestreAtual(2); // Bimestre atual padrão

// Aluno.java (campo com default)
private Integer bimestreAtual = 2;
```

### Por que isso é um problema

Um aluno que se cadastra em outubro entraria com `bimestreAtual = 2` (abril-junho), quando o bimestre correto seria 4 (outubro-dezembro). O dashboard mostraria dados do bimestre errado.

### Depois

Um método estático foi criado em `AlunoService` que calcula o bimestre com base na data atual do sistema:

```java
// AlunoService.java
public static int calcularBimestreAtual() {
    int mes = LocalDate.now().getMonthValue();
    if (mes <= 3) return 1;   // Janeiro, Fevereiro, Março
    if (mes <= 6) return 2;   // Abril, Maio, Junho
    if (mes <= 9) return 3;   // Julho, Agosto, Setembro
    return 4;                  // Outubro, Novembro, Dezembro
}
```

E usado no momento do cadastro:

```java
aluno.setBimestreAtual(AlunoService.calcularBimestreAtual());
```

O default `= 2` na entidade foi mantido como fallback, mas o registro real sempre usa o cálculo dinâmico.

---

## Problema 5 — `DataInitializer` rodando em produção

### Antes

```java
@Component // nenhum guard de perfil
public class DataInitializer implements CommandLineRunner {
    // cria mateus@pontua.com / 123456 em qualquer ambiente
}
```

### Por que isso é um problema

`CommandLineRunner` executa automaticamente ao subir a aplicação. Em produção, isso criaria uma conta de teste com senha fraca (`123456`) em todo deploy.

### Depois

```java
@Profile("dev")     // só executa quando SPRING_PROFILES_ACTIVE=dev
@Component
public class DataInitializer implements CommandLineRunner { ... }
```

Com o `docker-compose.yml` já configurado com `SPRING_PROFILES_ACTIVE: prod`, o `DataInitializer` nunca roda via Docker. Só roda em desenvolvimento local, onde é útil.

A anotação `@Profile` aceita expressões: `@Profile("dev")`, `@Profile("!prod")`, `@Profile({"dev", "test"})`.

---

## Nova exceção: `ConflictException`

Junto com as mudanças de service, foi criada `ConflictException` para representar tentativas de cadastro duplicado:

```java
@ResponseStatus(HttpStatus.CONFLICT) // HTTP 409
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
```

HTTP 409 (Conflict) é o status correto para "recurso já existe" — mais semântico que 400 (Bad Request). O `GlobalExceptionHandler` trata essa exceção e retorna um JSON padronizado:

```json
{ "error": "E-mail já cadastrado" }
```

O controller não precisa saber nada sobre isso — lança a exceção, o handler cuida do resto.

---

## Novo serviço: `DashboardService`

Foi criado para centralizar toda a lógica de montagem do dashboard, que antes estava espalhada no controller. Ele:

1. Busca o aluno pelo e-mail da sessão
2. Obtém a pontuação existente (cria registro zerado na primeira vez)
3. Busca notas, frequências e atividades **filtradas por bimestre** direto no banco
4. Monta e retorna o `DashboardDTO`

Tudo dentro de uma única transação `readOnly = true` — o banco sabe que não haverá escrita e pode otimizar.

---

## Diagrama do fluxo antes vs depois

**Antes (dashboard):**
```
GET /api/dashboard
  → DashboardController
      → AlunoService.buscarPorEmail()
      → PontuacaoService.calcularPontuacaoAluno()   ← ESCRITA
      → PontuacaoService.atualizarRankings()         ← ESCRITA (todos os alunos)
      → NotaRepository.findByAluno()                 ← lê tudo, filtra em Java
      → FrequenciaRepository.findByAluno()           ← lê tudo, filtra em Java
      → AtividadeExtraRepository.findByAluno()       ← lê tudo, filtra em Java
```

**Depois (dashboard):**
```
GET /api/dashboard
  → DashboardController
      → DashboardService.montarDashboard()
          → AlunoService.buscarPorEmail()
          → PontuacaoRepository.findByAluno()                      ← leitura simples
          → NotaRepository.findByAlunoAndBimestre()                ← filtrado no banco
          → FrequenciaRepository.findByAlunoAndBimestre()          ← filtrado no banco
          → AtividadeExtraRepository.findByAlunoAndBimestre()      ← filtrado no banco
```
