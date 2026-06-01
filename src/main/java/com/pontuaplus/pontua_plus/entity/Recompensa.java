package com.pontuaplus.pontua_plus.entity;

import com.pontuaplus.pontua_plus.enums.Ranking;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recompensas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Recompensa {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(length = 500)
    private String descricao;

    private String parceiro;

    @Enumerated(EnumType.STRING)
    @Column(name = "ranking_minimo", nullable = false)
    private Ranking rankingMinimo;

    private String icone;

    @Column(nullable = false)
    private Boolean ativo = true;
}
