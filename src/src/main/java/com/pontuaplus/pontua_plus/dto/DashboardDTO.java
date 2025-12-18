package com.pontuaplus.pontua_plus.dto;

import com.pontuaplus.pontua_plus.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private AlunoDTO aluno;
    private PontuacaoDTO pontuacao;
    private List<NotaDTO> notas;
    private List<FrequenciaDTO> frequencias;
    private List<AtividadeExtraDTO> atividadesExtras;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotaDTO {
        private String disciplina;
        private Double valor;
        private Integer bimestre;
        private Integer pontosConquistados;

        public static NotaDTO fromEntity(Nota nota) {
            return new NotaDTO(
                    nota.getDisciplina(),
                    nota.getValor(),
                    nota.getBimestre(),
                    nota.getPontosConquistados()
            );
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FrequenciaDTO {
        private Integer mes;
        private Integer ano;
        private Integer totalAulas;
        private Integer presencas;
        private Integer faltas;
        private Double percentualFrequencia;
        private Integer pontosConquistados;

        public static FrequenciaDTO fromEntity(Frequencia freq) {
            return new FrequenciaDTO(
                    freq.getMes(),
                    freq.getAno(),
                    freq.getTotalAulas(),
                    freq.getPresencas(),
                    freq.getFaltas(),
                    freq.getPercentualFrequencia(),
                    freq.getPontosConquistados()
            );
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AtividadeExtraDTO {
        private String nome;
        private String tipo;
        private Integer pontosConquistados;

        public static AtividadeExtraDTO fromEntity(AtividadeExtra atividade) {
            return new AtividadeExtraDTO(
                    atividade.getNome(),
                    atividade.getTipo().getDescricao(),
                    atividade.getPontosConquistados()
            );
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PontuacaoDTO {
        private Integer pontosNotas;
        private Integer pontosFrequencia;
        private Integer pontosExtras;
        private Integer totalPontos;
        private String ranking;
        private Integer posicaoRanking;

        public static PontuacaoDTO fromEntity(Pontuacao pontuacao) {
            return new PontuacaoDTO(
                    pontuacao.getPontosNotas(),
                    pontuacao.getPontosFrequencia(),
                    pontuacao.getPontosExtras(),
                    pontuacao.getTotalPontos(),
                    pontuacao.getRanking().name(),
                    pontuacao.getPosicaoRanking()
            );
        }
    }

    public static DashboardDTO fromEntities(Aluno aluno, Pontuacao pontuacao,
                                             List<Nota> notas, List<Frequencia> frequencias,
                                             List<AtividadeExtra> atividades) {
        return new DashboardDTO(
                AlunoDTO.fromEntity(aluno),
                pontuacao != null ? PontuacaoDTO.fromEntity(pontuacao) : null,
                notas.stream().map(NotaDTO::fromEntity).collect(Collectors.toList()),
                frequencias.stream().map(FrequenciaDTO::fromEntity).collect(Collectors.toList()),
                atividades.stream().map(AtividadeExtraDTO::fromEntity).collect(Collectors.toList())
        );
    }
}
