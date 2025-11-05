package com.pontuaplus.pontua_plus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "frequencias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Frequencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Min(value = 1, message = "Mês mínimo é 1")
    @Max(value = 12, message = "Mês máximo é 12")
    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer ano;

    @Min(value = 1, message = "Bimestre mínimo é 1")
    @Max(value = 4, message = "Bimestre máximo é 4")
    @Column(nullable = false)
    private Integer bimestre;

    @Column(name = "total_aulas", nullable = false)
    private Integer totalAulas;

    @Column(nullable = false)
    private Integer presencas;

    @Column(nullable = false)
    private Integer faltas;

    @Column(name = "percentual_frequencia")
    private Double percentualFrequencia;

    @Column(name = "pontos_conquistados")
    private Integer pontosConquistados;

    @PrePersist
    @PreUpdate
    private void calcularFrequenciaEPontos() {
        if (totalAulas > 0) {
            percentualFrequencia = (presencas * 100.0) / totalAulas;

            if (percentualFrequencia >= 95.0) {
                pontosConquistados = 15;
            } else if (percentualFrequencia >= 90.0) {
                pontosConquistados = 13;
            } else if (percentualFrequencia >= 80.0) {
                pontosConquistados = 11;
            } else {
                pontosConquistados = 0;
            }
        } else {
            percentualFrequencia = 0.0;
            pontosConquistados = 0;
        }
    }
}
