# 09 — Cards do Painel, Média da Turma

## Motivação

Pedido para reformar os 4 cards do topo do Desempenho seguindo uma referência visual enviada pelo usuário, adicionar a média da turma no gráfico de evolução das notas e trocar o preenchimento em gradiente das barras de progresso por dois blocos sólidos (azul/roxo). Durante a correção das barras surgiu um segundo pedido: auditar o código em busca de valores/cálculos duplicados — que revelou lógica de "próximo nível" reimplementada em mais de um lugar.

---

## Cards do topo do Desempenho

**`dashboard.html`** — card 1 ("Pontos Atuais") ganhou um badge de nível logo abaixo do valor (`<span class="tier-nivel-badge">`, reaproveitando as classes `badge-ouro/prata/bronze/diamond` que já existiam para Recompensas). O card 4 deixou de ser "Ranking Atual" (nome do tier + posição) e passou a ser "Próximo Nível": nome do próximo tier, na cor do próprio tier, com "X pontos para o próximo nível" embaixo.

Bug corrigido no processo: com `.metric-value` (48px), o texto "Diamante" estourava a largura do card. Trocado para `.metric-value-small` (32px), mesma escala das outras duas caixas.

---

## Média da turma no gráfico de notas

**`repository/NotaRepository.java`** — nova query `mediaPorBimestreDaTurma(turma, colegio)`, agregando `AVG(n.valor)` agrupado por bimestre para todos os alunos da mesma turma/colégio.

**`dto/DashboardDTO.java`** — novo campo `mediaTurma` (lista de `{bimestre, media}`), populado em `DashboardService.montarDashboard()`.

**`static/js/dashboard.js`** — `renderizarGraficoNotas()` ganhou um segundo dataset ("Média da Turma", linha tracejada roxa) no Chart.js, alinhado aos mesmos bimestres/meses do dataset do aluno. A legenda só aparece quando existe dado real de turma — se `mediaTurma` vier vazio (ex: aluno sem turma cadastrada), o gráfico volta ao comportamento anterior sem erro. "Média da Escola" não foi adicionada, como combinado, por só existir uma turma nos dados atuais.



---

## Duplicação de lógica de ranking (DRY)

Auditoria encontrou o cálculo de "próximo nível" reimplementado em três lugares com números de corte copiados à mão, sem nenhum vir do enum `Ranking` (fonte única de verdade: `BRONZE(0,25)`, `PRATA(26,64)`, `OURO(65,80)`, `DIAMOND(81,100)`):

1. `dashboard.js` (`NIVEIS` + `calcularProximoNivel()`, adicionado nesta mesma sessão) — array hardcoded com os 4 limiares.
2. `RecompensaService.proximoTier()` (já existia) — `switch` privado equivalente, nunca reaproveitado pelo dashboard.
3. `recompensas.js` (`getPontoMinimo()`, `tiers = ['DIAMOND','OURO','PRATA','BRONZE']`, strings `'81–100 pontos'` etc.) — reconstruía na mão dados que a própria API de Recompensas já mandava por tier.

**Correção:**

- **`enums/Ranking.java`** — novo método `getProximoNivel()` (switch simples: retorna o próximo enum, `null` se já é `DIAMOND`). Única implementação da regra no projeto.
- **`service/RecompensaService.java`** — método privado `proximoTier()` removido; chamada trocada para `rankingAtual.getProximoNivel()`.
- **`dto/DashboardDTO.java`** — `PontuacaoDTO` ganhou `proximoNivel` e `pontosParaProximo`, calculados em `fromEntity()` a partir de `Ranking.getProximoNivel()` + `getPontoMinimo()`. `dashboard.js` não calcula mais nada — só lê os dois campos prontos da API.
- **`dto/RecompensaDTO.java`** — `TierRecompensasDTO` ganhou `pontoMaximo` (o enum já tinha o valor, só faltava expor).
- **`static/js/recompensas.js`** — `getPontoMinimo()` removida (usa `tiers.find(t => t.tier === ...).pontoMinimo`); array `tiers` hardcoded removido (usa a posição real de `data.tiers`, que o backend já manda ordenado de `DIAMOND` a `BRONZE`); strings de faixa de pontos trocadas por `` `${tier.pontoMinimo}–${tier.pontoMaximo} pontos` ``.

Testado com Playwright depois da refatoração (dashboard e recompensas) — mesmos valores exibidos antes e depois (78 pontos, Ouro, "3 pontos para o próximo nível", faixas de tier corretas), confirmando que a extração não mudou nenhum resultado.

---

## Removido nesta sessão

O skip link "Pular para o conteúdo" também foi removido durante esta rodada (ver `08-aba-acessibilidade.md` para detalhes — foi removido junto com a correção das barras, na mesma conversa).

---

## Arquivos alterados

| Arquivo                            | Ação                                                                    |
| ---------------------------------- | ------------------------------------------------------------------------- |
| `enums/Ranking.java`             | `getProximoNivel()` adicionado                                          |
| `repository/NotaRepository.java` | `mediaPorBimestreDaTurma()` adicionado                                  |
| `dto/DashboardDTO.java`          | `mediaTurma`, `proximoNivel`, `pontosParaProximo` adicionados       |
| `dto/RecompensaDTO.java`         | `pontoMaximo` adicionado a `TierRecompensasDTO`                       |
| `service/DashboardService.java`  | Busca`mediaTurma` e passa pro DTO                                       |
| `service/RecompensaService.java` | `proximoTier()` removido, usa `Ranking.getProximoNivel()`             |
| `static/dashboard.html`          | Cards do topo reestruturados (badge + Próximo Nível)                    |
| `static/js/dashboard.js`         | Segunda linha no gráfico;`NIVEIS`/`calcularProximoNivel()` removidos |
| `static/js/recompensas.js`       | `getPontoMinimo()` e array `tiers` hardcoded removidos                |
| `static/css/styles.css`          | Barras: trilha roxa + preenchimento azul sólido, sem gradiente           |
