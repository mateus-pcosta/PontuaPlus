package com.pontuaplus.pontua_plus.service;

import com.pontuaplus.pontua_plus.entity.*;
import com.pontuaplus.pontua_plus.enums.TipoAtividade;
import com.pontuaplus.pontua_plus.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PontuacaoServiceTest {

    @Mock private PontuacaoRepository pontuacaoRepository;
    @Mock private NotaRepository notaRepository;
    @Mock private FrequenciaRepository frequenciaRepository;
    @Mock private AtividadeExtraRepository atividadeExtraRepository;

    @InjectMocks private PontuacaoService pontuacaoService;

    private Aluno aluno;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setBimestreAtual(2);
        when(pontuacaoRepository.findByAluno(aluno)).thenReturn(Optional.empty());
        when(pontuacaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void deveAtribuir35PontosParaMediaExcelente() {
        Nota nota = notaComValor(9.5, 2);
        when(notaRepository.findByAluno(aluno)).thenReturn(List.of(nota));
        when(frequenciaRepository.findByAluno(aluno)).thenReturn(Collections.emptyList());
        when(atividadeExtraRepository.findByAluno(aluno)).thenReturn(Collections.emptyList());

        Pontuacao result = pontuacaoService.calcularPontuacaoAluno(aluno);

        assertThat(result.getPontosNotas()).isEqualTo(35);
    }

    @Test
    void deveAtribuir25PontosParaMediaBoa() {
        Nota nota = notaComValor(7.5, 2);
        when(notaRepository.findByAluno(aluno)).thenReturn(List.of(nota));
        when(frequenciaRepository.findByAluno(aluno)).thenReturn(Collections.emptyList());
        when(atividadeExtraRepository.findByAluno(aluno)).thenReturn(Collections.emptyList());

        Pontuacao result = pontuacaoService.calcularPontuacaoAluno(aluno);

        assertThat(result.getPontosNotas()).isEqualTo(25);
    }

    @Test
    void deveAtribuirZeroPontosParaMediaAbaixoDaMinima() {
        Nota nota = notaComValor(4.0, 2);
        when(notaRepository.findByAluno(aluno)).thenReturn(List.of(nota));
        when(frequenciaRepository.findByAluno(aluno)).thenReturn(Collections.emptyList());
        when(atividadeExtraRepository.findByAluno(aluno)).thenReturn(Collections.emptyList());

        Pontuacao result = pontuacaoService.calcularPontuacaoAluno(aluno);

        assertThat(result.getPontosNotas()).isEqualTo(0);
    }

    @Test
    void deveAtribuir15PontosParaFrequenciaExcelente() {
        Frequencia freq = frequenciaComPercentual(97.0, 2);
        when(notaRepository.findByAluno(aluno)).thenReturn(Collections.emptyList());
        when(frequenciaRepository.findByAluno(aluno)).thenReturn(List.of(freq));
        when(atividadeExtraRepository.findByAluno(aluno)).thenReturn(Collections.emptyList());

        Pontuacao result = pontuacaoService.calcularPontuacaoAluno(aluno);

        assertThat(result.getPontosFrequencia()).isEqualTo(15);
    }

    @Test
    void deveAtribuir11PontosParaFrequenciaBoa() {
        Frequencia freq = frequenciaComPercentual(87.0, 2);
        when(notaRepository.findByAluno(aluno)).thenReturn(Collections.emptyList());
        when(frequenciaRepository.findByAluno(aluno)).thenReturn(List.of(freq));
        when(atividadeExtraRepository.findByAluno(aluno)).thenReturn(Collections.emptyList());

        Pontuacao result = pontuacaoService.calcularPontuacaoAluno(aluno);

        assertThat(result.getPontosFrequencia()).isEqualTo(11);
    }

    @Test
    void deveLimitarPontosExtrasEm50() {
        // 4 atividades de 15 pontos cada = 60 total, deve ser capped em 50
        List<AtividadeExtra> atividades = List.of(
                atividadeComPontos(15, 2),
                atividadeComPontos(15, 2),
                atividadeComPontos(15, 2),
                atividadeComPontos(15, 2)
        );
        when(notaRepository.findByAluno(aluno)).thenReturn(Collections.emptyList());
        when(frequenciaRepository.findByAluno(aluno)).thenReturn(Collections.emptyList());
        when(atividadeExtraRepository.findByAluno(aluno)).thenReturn(atividades);

        Pontuacao result = pontuacaoService.calcularPontuacaoAluno(aluno);

        assertThat(result.getPontosExtras()).isEqualTo(50);
    }

    @Test
    void deveIgnorarDadosDeOutroBimestre() {
        Nota notaOutroBimestre = notaComValor(10.0, 1); // bimestre 1, não o atual (2)
        when(notaRepository.findByAluno(aluno)).thenReturn(List.of(notaOutroBimestre));
        when(frequenciaRepository.findByAluno(aluno)).thenReturn(Collections.emptyList());
        when(atividadeExtraRepository.findByAluno(aluno)).thenReturn(Collections.emptyList());

        Pontuacao result = pontuacaoService.calcularPontuacaoAluno(aluno);

        assertThat(result.getPontosNotas()).isEqualTo(0);
    }

    @Test
    void deveCalcularMediaDeMultiplasNotas() {
        // Médias: 9.0 + 7.0 = média 8.0 → 32 pontos
        List<Nota> notas = List.of(notaComValor(9.0, 2), notaComValor(7.0, 2));
        when(notaRepository.findByAluno(aluno)).thenReturn(notas);
        when(frequenciaRepository.findByAluno(aluno)).thenReturn(Collections.emptyList());
        when(atividadeExtraRepository.findByAluno(aluno)).thenReturn(Collections.emptyList());

        Pontuacao result = pontuacaoService.calcularPontuacaoAluno(aluno);

        assertThat(result.getPontosNotas()).isEqualTo(32);
    }

    @Test
    void atualizarRankingsDeveAtribuirPosicoesPorOrdemDePontos() {
        Pontuacao p1 = new Pontuacao();
        p1.setTotalPontos(80);
        Pontuacao p2 = new Pontuacao();
        p2.setTotalPontos(50);
        Pontuacao p3 = new Pontuacao();
        p3.setTotalPontos(20);

        when(pontuacaoRepository.findAllOrderByTotalPontosDesc()).thenReturn(List.of(p1, p2, p3));

        pontuacaoService.atualizarRankings();

        assertThat(p1.getPosicaoRanking()).isEqualTo(1);
        assertThat(p2.getPosicaoRanking()).isEqualTo(2);
        assertThat(p3.getPosicaoRanking()).isEqualTo(3);
        verify(pontuacaoRepository, times(3)).save(any());
    }

    // Helpers

    private Nota notaComValor(double valor, int bimestre) {
        Nota nota = new Nota();
        nota.setValor(valor);
        nota.setBimestre(bimestre);
        return nota;
    }

    private Frequencia frequenciaComPercentual(double percentual, int bimestre) {
        Frequencia freq = new Frequencia();
        freq.setPercentualFrequencia(percentual);
        freq.setBimestre(bimestre);
        return freq;
    }

    private AtividadeExtra atividadeComPontos(int pontos, int bimestre) {
        AtividadeExtra atividade = new AtividadeExtra();
        atividade.setPontosConquistados(pontos);
        atividade.setBimestre(bimestre);
        atividade.setTipo(TipoAtividade.OUTRA);
        return atividade;
    }
}
