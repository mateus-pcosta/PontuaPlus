package com.pontuaplus.pontua_plus.service;

import com.pontuaplus.pontua_plus.entity.*;
import com.pontuaplus.pontua_plus.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return salva;
    }

    @Transactional
    public void atualizarRankings() {
        List<Pontuacao> pontuacoes = pontuacaoRepository.findAllOrderByTotalPontosDesc();
        for (int i = 0; i < pontuacoes.size(); i++) {
            pontuacoes.get(i).setPosicaoRanking(i + 1);
            pontuacaoRepository.save(pontuacoes.get(i));
        }
    }

    public Optional<Pontuacao> obterPontuacaoPorAluno(Long alunoId) {
        return pontuacaoRepository.findByAlunoId(alunoId);
    }

    public List<Pontuacao> obterRanking() {
        return pontuacaoRepository.findAllOrderByTotalPontosDesc();
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
