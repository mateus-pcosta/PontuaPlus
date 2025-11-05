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

        // Calcular pontos de notas do bimestre atual (média das notas)
        List<Nota> notasBimestre = notaRepository.findByAluno(aluno).stream()
                .filter(n -> n.getBimestre().equals(bimestreAtual))
                .toList();

        int pontosNotas = 0;
        if (!notasBimestre.isEmpty()) {
            double mediaNotas = notasBimestre.stream()
                    .mapToDouble(Nota::getValor)
                    .average()
                    .orElse(0.0);

            // Calcular pontos baseado na média (máximo 35 pontos)
            pontosNotas = calcularPontosPorMedia(mediaNotas);
        }
        pontuacao.setPontosNotas(pontosNotas);

        // Calcular pontos de frequência do bimestre atual (média dos meses)
        List<Frequencia> frequenciasBimestre = frequenciaRepository.findByAluno(aluno).stream()
                .filter(f -> f.getBimestre().equals(bimestreAtual))
                .toList();

        int pontosFrequencia = 0;
        if (!frequenciasBimestre.isEmpty()) {
            double mediaFrequencia = frequenciasBimestre.stream()
                    .mapToDouble(Frequencia::getPercentualFrequencia)
                    .average()
                    .orElse(0.0);

            // Calcular pontos baseado na frequência média (máximo 15 pontos)
            pontosFrequencia = calcularPontosPorFrequencia(mediaFrequencia);
        }
        pontuacao.setPontosFrequencia(pontosFrequencia);

        // Calcular pontos de atividades extras do bimestre atual (máximo 50 pontos)
        List<AtividadeExtra> atividades = atividadeExtraRepository.findByAluno(aluno).stream()
                .filter(a -> a.getBimestre().equals(bimestreAtual))
                .toList();

        int pontosExtras = atividades.stream()
                .mapToInt(AtividadeExtra::getPontosConquistados)
                .sum();

        // Limitar a 50 pontos
        pontosExtras = Math.min(pontosExtras, 50);
        pontuacao.setPontosExtras(pontosExtras);

        // Salvar pontuação (o cálculo total e ranking é feito automaticamente no @PrePersist/@PreUpdate)
        return pontuacaoRepository.save(pontuacao);
    }

    private int calcularPontosPorMedia(double media) {
        if (media >= 9.0) {
            return 35;
        } else if (media >= 8.0) {
            return 32;
        } else if (media >= 7.0) {
            return 25;
        } else if (media >= 6.0) {
            return 20;
        } else if (media >= 5.0) {
            return 15;
        } else {
            return 0;
        }
    }

    private int calcularPontosPorFrequencia(double percentual) {
        if (percentual >= 95.0) {
            return 15;
        } else if (percentual >= 90.0) {
            return 13;
        } else if (percentual >= 85.0) {
            return 11;
        } else if (percentual >= 80.0) {
            return 9;
        } else if (percentual >= 75.0) {
            return 7;
        } else {
            return 0;
        }
    }

    @Transactional
    public void atualizarRankings() {
        List<Pontuacao> pontuacoes = pontuacaoRepository.findAllOrderByTotalPontosDesc();

        for (int i = 0; i < pontuacoes.size(); i++) {
            Pontuacao pontuacao = pontuacoes.get(i);
            pontuacao.setPosicaoRanking(i + 1);
            pontuacaoRepository.save(pontuacao);
        }
    }

    public Pontuacao obterPontuacaoPorAluno(Long alunoId) {
        return pontuacaoRepository.findByAlunoId(alunoId)
                .orElse(null);
    }

    public List<Pontuacao> obterRanking() {
        return pontuacaoRepository.findAllOrderByTotalPontosDesc();
    }
}
