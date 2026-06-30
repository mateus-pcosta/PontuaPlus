# Refatoração das Anotações Lombok nas Entidades JPA

## O que foi mudado

Em todas as 7 entidades JPA (`Usuario`, `Aluno`, `Colaborador`, `Nota`, `Frequencia`, `AtividadeExtra`, `Pontuacao`), a anotação `@Data` foi substituída por anotações individuais com configuração explícita.

**Antes:**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Aluno extends Usuario { ... }
```

**Depois:**
```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"notas", "frequencias", "atividadesExtras", "pontuacao"})
public class Aluno extends Usuario { ... }
```

---

## Por que `@Data` é problemático em entidades JPA?

`@Data` é um atalho do Lombok que combina automaticamente:
- `@Getter` — gera getters para todos os campos
- `@Setter` — gera setters para todos os campos
- `@RequiredArgsConstructor` — construtor com campos `final`
- `@ToString` — gera `toString()` com todos os campos
- `@EqualsAndHashCode` — gera `equals()` e `hashCode()` com todos os campos

O problema não é o que `@Data` faz — é o que ele faz **sem você perceber**, especialmente em entidades JPA.

---

## Problema 1: `@EqualsAndHashCode` em todos os campos

### O que acontece
Por padrão, `@Data` gera `equals()` e `hashCode()` baseados em **todos** os campos da classe. Para uma entidade JPA, isso é perigoso.

### Por que é perigoso
Considere este cenário:

```java
Aluno aluno = new Aluno();
aluno.setNome("João");
aluno.setEmail("joao@email.com");
// id ainda é null (entidade nova, não persistida)

Set<Aluno> conjunto = new HashSet<>();
conjunto.add(aluno);

alunoRepository.save(aluno);
// agora aluno.id = 1 (gerado pelo banco)

conjunto.contains(aluno); // retorna FALSE!
```

Por quê? Porque o `hashCode` é calculado com base nos campos, incluindo o `id`. Antes de salvar, `id = null`. Depois de salvar, `id = 1`. O `hashCode` mudou — e o objeto "sumiu" do `HashSet`.

Isso causa bugs silenciosos difíceis de rastrear em qualquer código que use entidades em coleções (`Set`, `HashMap`).

### A solução
Basear `equals` e `hashCode` **apenas no `id`**, que é o identificador único e imutável da entidade:

```java
// Em entidades sem herança (Nota, Frequencia, etc.):
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Nota {
    @EqualsAndHashCode.Include
    @Id
    private Long id;
}

// Em entidades filhas (Aluno, Colaborador):
@EqualsAndHashCode(callSuper = true) // herda o equals do pai (Usuario)
public class Aluno extends Usuario { ... }

// Na classe pai (Usuario):
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {
    @EqualsAndHashCode.Include
    @Id
    private Long id;
}
```

Assim, dois objetos `Aluno` são iguais se e somente se têm o mesmo `id` — que é exatamente a semântica correta para uma entidade de banco de dados.

---

## Problema 2: `@ToString` em relacionamentos lazy causa `LazyInitializationException`

### O que é lazy loading
Quando você tem um relacionamento `@OneToMany` com `fetch = FetchType.LAZY` (o padrão), o Hibernate **não carrega** os dados relacionados do banco imediatamente. Ele carrega sob demanda — quando você acessa a coleção pela primeira vez.

```java
// Em Aluno.java
@OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Nota> notas = new ArrayList<>(); // lazy por padrão
```

### O que `@ToString` faz de errado
`@Data` gera um `toString()` que inclui **todos** os campos, incluindo as coleções lazy. Quando `toString()` é chamado fora de uma transação ativa (por exemplo, ao logar um objeto), o Hibernate tenta carregar as notas — mas não há mais conexão de banco aberta.

**Resultado:**
```
org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role:
com.pontuaplus.pontua_plus.entity.Aluno.notas,
could not initialize proxy - no Session
```

Esse erro é especialmente traiçoeiro porque aparece em lugares inesperados — em um `log.info("Aluno: {}", aluno)` que parece inofensivo.

### Além disso: referências circulares
`Aluno` tem `List<Nota>`, e cada `Nota` tem `Aluno`. Se `toString()` incluir ambos, você entra em recursão infinita:

```
Aluno → notas → [Nota → aluno → Aluno → notas → [Nota → aluno → ...]]
```

Resultado: `StackOverflowError`.

### A solução
Excluir explicitamente os campos que causam problema:

```java
// Entidades pai com coleções lazy
@ToString(callSuper = true, exclude = {"notas", "frequencias", "atividadesExtras", "pontuacao"})
public class Aluno extends Usuario { ... }

// Entidades filhas com referência ao pai (bidirecional)
@ToString(exclude = "aluno")
public class Nota { ... }
```

---

## Problema 3 (bônus): `@ToString` expõe a senha

Com `@Data`, o `toString()` de `Usuario` incluiria o campo `senha` (o hash BCrypt). Se você logar um objeto `Usuario`, o hash vai aparecer nos logs:

```
Usuario(id=1, nome=Mateus, email=mateus@pontua.com, senha=$2a$10$N9qo8..., tipo=ALUNO)
```

Isso não é a senha em texto puro, mas expõe informação sensível desnecessariamente.

**Solução:**
```java
@ToString(exclude = "senha")
public class Usuario { ... }
```

---

## Resumo das decisões por entidade

| Entidade | `equals/hashCode` | `toString` excluído |
|---|---|---|
| `Usuario` | `onlyExplicitlyIncluded` — baseado em `id` | `senha` |
| `Aluno` | `callSuper = true` — herda de Usuario | `notas`, `frequencias`, `atividadesExtras`, `pontuacao` |
| `Colaborador` | `callSuper = true` | nada (sem coleções lazy) |
| `Nota` | `onlyExplicitlyIncluded` — baseado em `id` | `aluno` |
| `Frequencia` | `onlyExplicitlyIncluded` — baseado em `id` | `aluno` |
| `AtividadeExtra` | `onlyExplicitlyIncluded` — baseado em `id` | `aluno` |
| `Pontuacao` | `onlyExplicitlyIncluded` — baseado em `id` | `aluno` |

---

## Por que manter `@Data` nos DTOs?

DTOs (`AlunoDTO`, `DashboardDTO`, etc.) são simples objetos de transferência de dados — sem lógica JPA, sem lazy loading, sem herança complexa. Para eles, `@Data` é exatamente o que se quer: getters, setters, equals, hashCode e toString em todos os campos. Nenhum dos problemas acima se aplica.
