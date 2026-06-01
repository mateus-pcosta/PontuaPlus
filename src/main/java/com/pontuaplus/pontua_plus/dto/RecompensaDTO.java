package com.pontuaplus.pontua_plus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecompensaDTO {

    private StatusAtualDTO statusAtual;
    private List<TierRecompensasDTO> tiers;
    private List<EmblemaDTO> emblemas;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusAtualDTO {
        private int totalPontos;
        private String rankingAtual;
        private String proximoNivel;
        private int pontosParaProximo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TierRecompensasDTO {
        private String tier;
        private int pontoMinimo;
        private boolean desbloqueado;
        private List<RecompensaItemDTO> recompensas;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecompensaItemDTO {
        private Long id;
        private String nome;
        private String descricao;
        private String parceiro;
        private String icone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmblemaDTO {
        private String titulo;
        private String ranking;
        private int bimestre;
        private int ano;
    }
}
