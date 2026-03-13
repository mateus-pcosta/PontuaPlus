package com.pontuaplus.pontua_plus.enums;

import lombok.Getter;

@Getter
public enum TipoAtividade {
    // Liderança e Responsabilidade
    LIDER_TURMA("Líder de Turma", 10),
    SISTEMA_COMPANHEIROS("Sistema de Companheiros", 10),

    // Olimpíadas e Competições
    OBMEP("Participação na OBMEP", 5),
    OLIMPIADA_MATEMATICA("Olimpíada de Matemática", 5),
    OLIMPIADA_OUTRAS("Outras Olimpíadas", 5),

    // Clubes e Atividades
    CLUBE_ROBOTICA("Clube de Robótica", 10),
    CLUBE_DEBATE("Clube de Debate", 10),
    CLUBE_CIENCIAS("Clube de Ciências", 10),

    // Criação de Conteúdo
    CRIACAO_CONTEUDO_AUDIO("Criação de Conteúdo de Áudio", 15),
    CRIACAO_CONTEUDO_VIDEO("Criação de Conteúdo de Vídeo", 15),
    CRIACAO_CONTEUDO_ESCRITO("Criação de Conteúdo Escrito", 10),

    // Voluntariado
    VOLUNTARIADO("Voluntariado", 10),

    // Outras
    OUTRA("Outra Atividade", 5);

    private final String descricao;
    private final int pontos;

    TipoAtividade(String descricao, int pontos) {
        this.descricao = descricao;
        this.pontos = pontos;
    }
}
