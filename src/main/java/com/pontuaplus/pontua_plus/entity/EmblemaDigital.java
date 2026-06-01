package com.pontuaplus.pontua_plus.entity;

import com.pontuaplus.pontua_plus.enums.Ranking;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "emblemas_digitais",
        uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "bimestre", "ano"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "aluno")
public class EmblemaDigital {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Column(nullable = false)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Ranking ranking;

    @Column(nullable = false)
    private Integer bimestre;

    @Column(nullable = false)
    private Integer ano;

    @Column(name = "conquistado_em")
    private LocalDateTime conquistadoEm;
}
