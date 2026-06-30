# Tela de Ranking com Controle de Acesso por Tier

## O que foi adicionado

Uma tela de ranking completa (`/ranking.html`) acessível a todos os alunos autenticados. O backend expõe um endpoint `GET /api/ranking` que retorna os 4 tiers (Diamond, Ouro, Prata, Bronze) com suas listas de alunos — mas com uma restrição: **alunos só enxergam dados dos tiers iguais ou superiores ao seu próprio nível**.

---

## Por que restringir o acesso por tier?

Expor todos os alunos de todos os tiers a qualquer usuário autenticado levanta dois problemas:

1. **Privacidade**: um aluno no topo (Diamond) poderia ver os dados de todos os colegas em tiers inferiores, mesmo aqueles com desempenho ruim, sem que esses colegas tenham qualquer visibilidade sobre o Diamond.
2. **Motivação**: a restrição cria um incentivo natural — para ver quem está no tier acima, o aluno precisa subir de nível. Isso é um mecanismo de gamificação, não apenas uma proteção técnica.

A regra implementada é assimétrica intencional: quem está acima pode ver quem está abaixo de si no próprio tier, mas não o contrário.

---

## Regra de acessibilidade (implementada em `PontuacaoService`)

```java
private List<Ranking> tiersAcessiveis(Ranking meuTier) {
    return switch (meuTier) {
        case DIAMOND -> List.of(Ranking.DIAMOND);
        case OURO    -> List.of(Ranking.DIAMOND, Ranking.OURO);
        case PRATA   -> List.of(Ranking.DIAMOND, Ranking.OURO, Ranking.PRATA);
        case BRONZE  -> List.of(Ranking.DIAMOND, Ranking.OURO, Ranking.PRATA, Ranking.BRONZE);
    };
}
```

Um aluno Bronze vê todos os tiers. Um aluno Diamond vê apenas o Diamond. Isso reflete o princípio de que quem está no topo já chegou — a descoberta acontece na subida.

---

## Como a restrição é aplicada no DTO

Tiers inacessíveis são **sempre retornados** na resposta (para que o frontend renderize os cards bloqueados), mas com `acessivel: false` e `alunos: []` — nenhum nome ou pontuação de outros alunos é transmitido.

```java
List<AlunoRankingDTO> alunosDTO = acessivel
    ? pontuacaoRepository.findByRankingOrderByTotalPontosDesc(tier).stream()
          .map(p -> new AlunoRankingDTO(
              p.getAluno().getNome(),
              p.getTotalPontos(),
              p.getPosicaoRanking(),
              p.getAluno().getId().equals(aluno.getId())))
          .toList()
    : List.of();
```

O `AlunoRankingDTO` expõe apenas: `nome`, `totalPontos`, `posicaoRanking` e `euMesmo`. Nenhum dado sensível (e-mail, CPF, matrícula, senha).

---

## Por que `Authentication` como parâmetro em vez de `SecurityContextHolder`?

```java
// Antes (acoplado ao contexto estático)
String email = SecurityContextHolder.getContext().getAuthentication().getName();

// Depois (injetado pelo Spring MVC)
@GetMapping
public ResponseEntity<RankingDTO> getRanking(Authentication authentication) {
    return ResponseEntity.ok(pontuacaoService.montarRanking(authentication.getName()));
}
```

O Spring MVC injeta o `Authentication` automaticamente quando declarado como parâmetro. Isso torna o controller mais fácil de testar (o objeto pode ser passado diretamente no teste sem precisar configurar o `SecurityContextHolder`) e deixa explícito que o método depende de um usuário autenticado.

---

## Estrutura do DTO

```
RankingDTO
├── meuRanking: "OURO"
├── minhaPosicao: 5
└── tiers: [
      TierDTO { nome: "DIAMOND", faixaPontos: "81-100 pontos", totalAlunos: 3,
                percentual: 18, acessivel: true,
                alunos: [ AlunoRankingDTO { nome, totalPontos, posicaoRanking, euMesmo } ] },
      TierDTO { nome: "OURO",    ..., acessivel: true,  alunos: [...] },
      TierDTO { nome: "PRATA",   ..., acessivel: false, alunos: [] },
      TierDTO { nome: "BRONZE",  ..., acessivel: false, alunos: [] }
    ]
```

O frontend usa o campo `acessivel` para decidir se renderiza o card como clicável (expande a lista) ou bloqueado (opacity reduzida, ícone de cadeado, sem interação).

---

## Comportamento no frontend (`ranking.js`)

- Cards de tiers acessíveis: clicáveis, expandem a lista de alunos ao clicar
- O tier do próprio aluno: auto-expandido ao carregar, com badge "Seu Nível"
- Aluno identificado como `euMesmo: true`: destaque visual na lista e sufixo "(você)"
- Cards bloqueados: visual com opacity reduzida, ícone de cadeado, sem listener de clique
- `escapeHtml()` aplicado em todos os dados antes de inserção no DOM (proteção XSS)
