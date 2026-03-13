package com.pontuaplus.pontua_plus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @NotBlank(message = "Disciplina é obrigatória")
    @Column(nullable = false)
    private String disciplina;

    @Min(value = 0, message = "Nota mínima é 0")
    @Max(value = 10, message = "Nota máxima é 10")
    @Column(nullable = false)
    private Double valor;

    @Min(value = 1, message = "Bimestre mínimo é 1")
    @Max(value = 4, message = "Bimestre máximo é 4")
    @Column(nullable = false)
    private Integer bimestre;

    @Column(name = "pontos_conquistados")
    private Integer pontosConquistados;

    @PrePersist
    @PreUpdate
    private void calcularPontos() {
        if (valor >= 9.0) {
            pontosConquistados = 35;
        } else if (valor >= 8.0) {
            pontosConquistados = 32;
        } else if (valor >= 7.0) {
            pontosConquistados = 25;
        } else if (valor >= 6.0) {
            pontosConquistados = 20;
        } else {
            pontosConquistados = 0;
        }
    }
}
