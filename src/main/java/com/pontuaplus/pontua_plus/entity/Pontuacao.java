package com.pontuaplus.pontua_plus.entity;

import com.pontuaplus.pontua_plus.enums.Ranking;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pontuacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "aluno")
public class Pontuacao {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false, unique = true)
    private Aluno aluno;

    @Column(name = "pontos_notas")
    private Integer pontosNotas = 0;

    @Column(name = "pontos_frequencia")
    private Integer pontosFrequencia = 0;

    @Column(name = "pontos_extras")
    private Integer pontosExtras = 0;

    @Column(name = "total_pontos")
    private Integer totalPontos = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Ranking ranking = Ranking.BRONZE;

    @Column(name = "posicao_ranking")
    private Integer posicaoRanking;

    @PrePersist
    @PreUpdate
    private void calcularTotalERanking() {
        totalPontos = pontosNotas + pontosFrequencia + pontosExtras;
        ranking = Ranking.getRankingByPontos(totalPontos);
    }
}
