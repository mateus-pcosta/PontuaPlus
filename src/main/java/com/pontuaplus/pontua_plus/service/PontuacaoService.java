package com.pontuaplus.pontua_plus.service;

import com.pontuaplus.pontua_plus.dto.RankingDTO;
import com.pontuaplus.pontua_plus.dto.RankingDTO.AlunoRankingDTO;
import com.pontuaplus.pontua_plus.dto.RankingDTO.TierDTO;
import com.pontuaplus.pontua_plus.entity.*;
import com.pontuaplus.pontua_plus.enums.Ranking;
import com.pontuaplus.pontua_plus.exception.ResourceNotFoundException;
import com.pontuaplus.pontua_plus.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PontuacaoService {

    private static final double NOTA_EXCELENTE = 9.0;
    private static final double NOTA_OTIMA     = 8.0;
    private static final double NOTA_BOA       = 7.0;
    private static final double NOTA_REGULAR   = 6.0;
    private static final double NOTA_MINIMA    = 5.0;
    private static final int PONTOS_NOTA_EXCELENTE = 35;
    private static final int PONTOS_NOTA_OTIMA     = 32;
    private static final int PONTOS_NOTA_BOA       = 25;
    private static final int PONTOS_NOTA_REGULAR   = 20;
    private static final int PONTOS_NOTA_MINIMA    = 15;

    private static final double FREQ_EXCELENTE = 95.0;
    private static final double FREQ_OTIMA     = 90.0;
    private static final double FREQ_BOA       = 85.0;
    private static final double FREQ_REGULAR   = 80.0;
    private static final double FREQ_MINIMA    = 75.0;
    private static final int PONTOS_FREQ_EXCELENTE = 15;
    private static final int PONTOS_FREQ_OTIMA     = 13;
    private static final int PONTOS_FREQ_BOA       = 11;
    private static final int PONTOS_FREQ_REGULAR   = 9;
    private static final int PONTOS_FREQ_MINIMA    = 7;

    private static final int MAX_PONTOS_EXTRAS = 50;

    private final PontuacaoRepository pontuacaoRepository;
    private final NotaRepository notaRepository;
    private final FrequenciaRepository frequenciaRepository;
    private final AtividadeExtraRepository atividadeExtraRepository;
    private final AlunoRepository alunoRepository;
    private final EmblemaDigitalRepository emblemaDigitalRepository;

    @Transactional(readOnly = true)
    public Optional<Pontuacao> buscarPorAluno(Aluno aluno) {
        return pontuacaoRepository.findByAluno(aluno);
    }

    @Transactional
    public Pontuacao calcularPontuacaoAluno(Aluno aluno) {
        Pontuacao pontuacao = pontuacaoRepository.findByAluno(aluno)
                .orElse(new Pontuacao());

        pontuacao.setAluno(aluno);

        int bimestre = aluno.getBimestreAtual() != null ? aluno.getBimestreAtual() : 2;

        List<Nota> notas = notaRepository.findByAlunoAndBimestre(aluno, bimestre);
        int pontosNotas = 0;
        if (!notas.isEmpty()) {
            double media = notas.stream().mapToDouble(Nota::getValor).average().orElse(0.0);
            pontosNotas = calcularPontosPorMedia(media);
        }
        pontuacao.setPontosNotas(pontosNotas);

        List<Frequencia> frequencias = frequenciaRepository.findByAlunoAndBimestre(aluno, bimestre);
        int pontosFrequencia = 0;
        if (!frequencias.isEmpty()) {
            double media = frequencias.stream()
                    .mapToDouble(Frequencia::getPercentualFrequencia).average().orElse(0.0);
            pontosFrequencia = calcularPontosPorFrequencia(media);
        }
        pontuacao.setPontosFrequencia(pontosFrequencia);

        List<AtividadeExtra> atividades = atividadeExtraRepository.findByAlunoAndBimestre(aluno, bimestre);
        int pontosExtras = Math.min(
                atividades.stream().mapToInt(AtividadeExtra::getPontosConquistados).sum(),
                MAX_PONTOS_EXTRAS
        );
        pontuacao.setPontosExtras(pontosExtras);

        Pontuacao salva = pontuacaoRepository.save(pontuacao);
        atualizarRankings();
        criarEmblemaSeNecessario(aluno, salva, bimestre);
        return salva;
    }

    private void criarEmblemaSeNecessario(Aluno aluno, Pontuacao pontuacao, int bimestre) {
        int ano = LocalDate.now().getYear();
        if (!emblemaDigitalRepository.existsByAlunoAndBimestreAndAno(aluno, bimestre, ano)) {
            EmblemaDigital emblema = new EmblemaDigital();
            emblema.setAluno(aluno);
            emblema.setRanking(pontuacao.getRanking());
            emblema.setBimestre(bimestre);
            emblema.setAno(ano);
            emblema.setTitulo("Emblema " + formatarNomeTier(pontuacao.getRanking()) + " " + ano + "/" + bimestre);
            emblema.setConquistadoEm(LocalDateTime.now());
            emblemaDigitalRepository.save(emblema);
        }
    }

    private String formatarNomeTier(Ranking ranking) {
        return switch (ranking) {
            case BRONZE  -> "Bronze";
            case PRATA   -> "Prata";
            case OURO    -> "Ouro";
            case DIAMOND -> "Diamante";
        };
    }

    @Transactional
    public void atualizarRankings() {
        List<Pontuacao> pontuacoes = pontuacaoRepository.findAllOrderByTotalPontosDesc();
        for (int i = 0; i < pontuacoes.size(); i++) {
            pontuacoes.get(i).setPosicaoRanking(i + 1);
        }
        pontuacaoRepository.saveAll(pontuacoes);
    }

    public Optional<Pontuacao> obterPontuacaoPorAluno(Long alunoId) {
        return pontuacaoRepository.findByAlunoId(alunoId);
    }

    public List<Pontuacao> obterRanking() {
        return pontuacaoRepository.findAllOrderByTotalPontosDesc();
    }

    @Transactional(readOnly = true)
    public RankingDTO montarRanking(String emailAluno) {
        Aluno aluno = alunoRepository.findByEmail(emailAluno)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));
        Pontuacao minhaPontuacao = pontuacaoRepository.findByAluno(aluno)
                .orElseThrow(() -> new ResourceNotFoundException("Pontuação não encontrada"));

        Ranking meuTier = minhaPontuacao.getRanking();
        if (meuTier == null) {
            throw new ResourceNotFoundException("Pontuação do aluno ainda não foi calculada");
        }
        List<Ranking> acessiveis = tiersAcessiveis(meuTier);
        long totalAlunos = pontuacaoRepository.count();

        List<TierDTO> tiers = Arrays.stream(Ranking.values())
                .sorted((a, b) -> b.getPontoMinimo() - a.getPontoMinimo())
                .map(tier -> {
                    long qtd = pontuacaoRepository.countByRanking(tier);
                    int percentual = totalAlunos > 0 ? (int) Math.round(qtd * 100.0 / totalAlunos) : 0;
                    boolean acessivel = acessiveis.contains(tier);
                    String faixa = tier.getPontoMinimo() + "-" + tier.getPontoMaximo() + " pontos";

                    List<AlunoRankingDTO> alunosDTO = acessivel
                            ? pontuacaoRepository.findByRankingOrderByTotalPontosDesc(tier).stream()
                                    .map(p -> new AlunoRankingDTO(
                                            p.getAluno().getNome(),
                                            p.getTotalPontos(),
                                            p.getPosicaoRanking(),
                                            p.getAluno().getId().equals(aluno.getId())))
                                    .toList()
                            : List.of();

                    return new TierDTO(tier.name(), faixa, (int) qtd, percentual, acessivel, alunosDTO);
                })
                .toList();

        return new RankingDTO(meuTier.name(), minhaPontuacao.getPosicaoRanking(), tiers);
    }

    private List<Ranking> tiersAcessiveis(Ranking meuTier) {
        return switch (meuTier) {
            case DIAMOND -> List.of(Ranking.DIAMOND);
            case OURO    -> List.of(Ranking.DIAMOND, Ranking.OURO);
            case PRATA   -> List.of(Ranking.DIAMOND, Ranking.OURO, Ranking.PRATA);
            case BRONZE  -> List.of(Ranking.DIAMOND, Ranking.OURO, Ranking.PRATA, Ranking.BRONZE);
        };
    }

    private int calcularPontosPorMedia(double media) {
        if (media >= NOTA_EXCELENTE) return PONTOS_NOTA_EXCELENTE;
        if (media >= NOTA_OTIMA)     return PONTOS_NOTA_OTIMA;
        if (media >= NOTA_BOA)       return PONTOS_NOTA_BOA;
        if (media >= NOTA_REGULAR)   return PONTOS_NOTA_REGULAR;
        if (media >= NOTA_MINIMA)    return PONTOS_NOTA_MINIMA;
        return 0;
    }

    private int calcularPontosPorFrequencia(double percentual) {
        if (percentual >= FREQ_EXCELENTE) return PONTOS_FREQ_EXCELENTE;
        if (percentual >= FREQ_OTIMA)     return PONTOS_FREQ_OTIMA;
        if (percentual >= FREQ_BOA)       return PONTOS_FREQ_BOA;
        if (percentual >= FREQ_REGULAR)   return PONTOS_FREQ_REGULAR;
        if (percentual >= FREQ_MINIMA)    return PONTOS_FREQ_MINIMA;
        return 0;
    }
}
