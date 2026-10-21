package com.pontuaplus.pontua_plus.enums;

import lombok.Getter;

@Getter
public enum TipoAtividade {
    LIDER_TURMA("Líder de Turma", 10, "Liderança e Responsabilidade"),
    SISTEMA_COMPANHEIROS("Sistema de Companheiros", 10, "Liderança e Responsabilidade"),

    OBMEP("Participação na OBMEP", 5, "Olimpíadas e Competições"),
    OLIMPIADA_MATEMATICA("Olimpíada de Matemática", 5, "Olimpíadas e Competições"),
    OLIMPIADA_OUTRAS("Outras Olimpíadas", 5, "Olimpíadas e Competições"),

    CLUBE_ROBOTICA("Clube de Robótica", 10, "Clubes e Atividades"),
    CLUBE_DEBATE("Clube de Debate", 10, "Clubes e Atividades"),
    CLUBE_CIENCIAS("Clube de Ciências", 10, "Clubes e Atividades"),

    CRIACAO_CONTEUDO_AUDIO("Criação de Conteúdo de Áudio", 15, "Criação de Conteúdo"),
    CRIACAO_CONTEUDO_VIDEO("Criação de Conteúdo de Vídeo", 15, "Criação de Conteúdo"),
    CRIACAO_CONTEUDO_ESCRITO("Criação de Conteúdo Escrito", 10, "Criação de Conteúdo"),

    VOLUNTARIADO("Voluntariado", 10, "Voluntariado"),

    OUTRA("Outra Atividade", 5, "Outras");

    private final String descricao;
    private final int pontos;
    private final String categoria;

    TipoAtividade(String descricao, int pontos, String categoria) {
        this.descricao = descricao;
        this.pontos = pontos;
        this.categoria = categoria;
    }
}
