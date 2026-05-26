package com.pontuaplus.pontua_plus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RankingDTO {

    private String meuRanking;
    private int minhaPosicao;
    private List<TierDTO> tiers;

    @Getter
    @AllArgsConstructor
    public static class TierDTO {
        private String nome;
        private String faixaPontos;
        private int totalAlunos;
        private int percentual;
        private boolean acessivel;
        private List<AlunoRankingDTO> alunos;
    }

    @Getter
    @AllArgsConstructor
    public static class AlunoRankingDTO {
        private String nome;
        private int totalPontos;
        private int posicaoRanking;
        private boolean euMesmo;
    }
}
