package com.pontuaplus.pontua_plus.enums;

import lombok.Getter;

@Getter
public enum Ranking {
    BRONZE(0, 25),
    PRATA(26, 64),
    OURO(65, 80),
    DIAMOND(81, 100);

    private final int pontoMinimo;
    private final int pontoMaximo;

    Ranking(int pontoMinimo, int pontoMaximo) {
        this.pontoMinimo = pontoMinimo;
        this.pontoMaximo = pontoMaximo;
    }

    public static Ranking getRankingByPontos(int pontos) {
        for (Ranking ranking : values()) {
            if (pontos >= ranking.pontoMinimo && pontos <= ranking.pontoMaximo) {
                return ranking;
            }
        }
        return BRONZE;
    }
}
