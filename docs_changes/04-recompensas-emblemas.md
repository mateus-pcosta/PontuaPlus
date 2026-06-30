# Tela de Recompensas + Emblemas Digitais no Perfil

## O que foi adicionado

Uma tela de recompensas (`/recompensas.html`) e um sistema de emblemas digitais colecionáveis que aparecem no perfil do aluno. As recompensas são fictícias por enquanto — parcerias com empresas locais de Teresina/Timon — e cada tier tem suas próprias recompensas disponíveis. Bronze não tem recompensa material, apenas o emblema digital.

---

## Regra de desbloqueio de recompensas

O aluno só pode resgatar recompensas do **seu tier atual**. Tiers acima do seu: exibidos com visual bloqueado (cadeado, opacity reduzida). Tiers abaixo: exibidos como "Já conquistado".

Isso é diferente do ranking, onde tiers abaixo ficam completamente ocultos. Nas recompensas, todos os tiers são visíveis para motivar a progressão — o aluno pode ver o que ganharia se subisse de nível.

---

## Sistema de emblemas digitais

Um emblema é criado automaticamente ao final de `PontuacaoService.calcularPontuacaoAluno()`, usando a constraint `UNIQUE(aluno_id, bimestre, ano)` para garantir um emblema por bimestre por aluno:

```java
private void criarEmblemaSeNecessario(Aluno aluno, Pontuacao pontuacao, int bimestre) {
    int ano = LocalDate.now().getYear();
    if (!emblemaDigitalRepository.existsByAlunoAndBimestreAndAno(aluno, bimestre, ano)) {
        EmblemaDigital emblema = new EmblemaDigital();
        emblema.setTitulo("Emblema " + formatarNomeTier(pontuacao.getRanking()) + " " + ano + "/" + bimestre);
        // ...
        emblemaDigitalRepository.save(emblema);
    }
}
```

O título do emblema registra o tier no momento do cálculo: "Emblema Ouro 2024/3". Se o aluno subir de tier no próximo bimestre, um novo emblema com o novo tier é criado — formando um histórico colecionável.

---

## Endpoint `/api/recompensas/emblemas` separado

O perfil chama `GET /api/recompensas/emblemas` em vez de `GET /api/recompensas` completo, pois não precisa do catálogo de recompensas — apenas dos emblemas. Isso evita trafegar desnecessariamente a lista de recompensas a cada carregamento do perfil.

---

## Fix de flicker no card de status

A barra de progresso do card "Seu Status Atual" usava a mesma classe `.progress-fill` do dashboard, que tem `transition: width 1s` e uma `animation: shimmer`. Quando o `recompensasContent` passava de `display:none` para `display:block`, alguns navegadores "re-trigavam" a transição a partir de `width: 0%` (valor inicial no HTML), causando uma animação visível que fazia o card parecer piscar.

**Fix**: classe `.progress-fill-static` com `transition: none` e `animation: none` aplicada apenas nessa barra, sem afetar as demais barras da aplicação.

---

## Estrutura do DTO

```
RecompensaDTO
├── statusAtual: { totalPontos, rankingAtual, proximoNivel, pontosParaProximo }
├── tiers: [
│     TierRecompensasDTO { tier, pontoMinimo, desbloqueado, recompensas: [...] }
│     ... (DIAMOND → OURO → PRATA → BRONZE)
│   ]
└── emblemas: [ EmblemaDTO { titulo, ranking, bimestre, ano } ]
```

Bronze sempre retorna `recompensas: []` pois não há recompensas materiais cadastradas para esse tier. O frontend detecta isso e exibe a mensagem de emblema digital.
