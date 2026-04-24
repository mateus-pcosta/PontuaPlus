package com.pontuaplus.pontua_plus.service;

import com.pontuaplus.pontua_plus.entity.*;
import com.pontuaplus.pontua_plus.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PontuacaoService {

    // Thresholds e pontos para notas
    private static final double NOTA_EXCELENTE = 9.0;
    private static final double NOTA_OTIMA = 8.0;
    private static final double NOTA_BOA = 7.0;
    private static final double NOTA_REGULAR = 6.0;
    private static final double NOTA_MINIMA = 5.0;
    private static final int PONTOS_NOTA_EXCELENTE = 35;
    private static final int PONTOS_NOTA_OTIMA = 32;
    private static final int PONTOS_NOTA_BOA = 25;
    private static final int PONTOS_NOTA_REGULAR = 20;
    private static final int PONTOS_NOTA_MINIMA = 15;

    // Thresholds e pontos para frequência
    private static final double FREQ_EXCELENTE = 95.0;
    private static final double FREQ_OTIMA = 90.0;
    private static final double FREQ_BOA = 85.0;
    private static final double FREQ_REGULAR = 80.0;
    private static final double FREQ_MINIMA = 75.0;
    private static final int PONTOS_FREQ_EXCELENTE = 15;
    private static final int PONTOS_FREQ_OTIMA = 13;
    private static final int PONTOS_FREQ_BOA = 11;
    private static final int PONTOS_FREQ_REGULAR = 9;
    private static final int PONTOS_FREQ_MINIMA = 7;

    private static final int MAX_PONTOS_EXTRAS = 50;

    private final PontuacaoRepository pontuacaoRepository;
    private final NotaRepository notaRepository;
    private final FrequenciaRepository frequenciaRepository;
    private final AtividadeExtraRepository atividadeExtraRepository;

    @Transactional
    public Pontuacao calcularPontuacaoAluno(Aluno aluno) {
        Pontuacao pontuacao = pontuacaoRepository.findByAluno(aluno)
                .orElse(new Pontuacao());

        pontuacao.setAluno(aluno);

        Integer bimestreAtual = aluno.getBimestreAtual() != null ? aluno.getBimestreAtual() : 2;

        List<Nota> notasBimestre = notaRepository.findByAluno(aluno).stream()
                .filter(n -> n.getBimestre().equals(bimestreAtual))
                .toList();

        int pontosNotas = 0;
        if (!notasBimestre.isEmpty()) {
            double mediaNotas = notasBimestre.stream()
                    .mapToDouble(Nota::getValor)
                    .average()
                    .orElse(0.0);
            pontosNotas = calcularPontosPorMedia(mediaNotas);
        }
        pontuacao.setPontosNotas(pontosNotas);

        List<Frequencia> frequenciasBimestre = frequenciaRepository.findByAluno(aluno).stream()
                .filter(f -> f.getBimestre().equals(bimestreAtual))
                .toList();

        int pontosFrequencia = 0;
        if (!frequenciasBimestre.isEmpty()) {
            double mediaFrequencia = frequenciasBimestre.stream()
                    .mapToDouble(Frequencia::getPercentualFrequencia)
                    .average()
                    .orElse(0.0);
            pontosFrequencia = calcularPontosPorFrequencia(mediaFrequencia);
        }
        pontuacao.setPontosFrequencia(pontosFrequencia);

        List<AtividadeExtra> atividades = atividadeExtraRepository.findByAluno(aluno).stream()
                .filter(a -> a.getBimestre().equals(bimestreAtual))
                .toList();

        int pontosExtras = Math.min(
                atividades.stream().mapToInt(AtividadeExtra::getPontosConquistados).sum(),
                MAX_PONTOS_EXTRAS
        );
        pontuacao.setPontosExtras(pontosExtras);

        return pontuacaoRepository.save(pontuacao);
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

    @Transactional
    public void atualizarRankings() {
        List<Pontuacao> pontuacoes = pontuacaoRepository.findAllOrderByTotalPontosDesc();
        for (int i = 0; i < pontuacoes.size(); i++) {
            pontuacoes.get(i).setPosicaoRanking(i + 1);
            pontuacaoRepository.save(pontuacoes.get(i));
        }
    }

    public Pontuacao obterPontuacaoPorAluno(Long alunoId) {
        return pontuacaoRepository.findByAlunoId(alunoId).orElse(null);
    }

    public List<Pontuacao> obterRanking() {
        return pontuacaoRepository.findAllOrderByTotalPontosDesc();
    }
}
