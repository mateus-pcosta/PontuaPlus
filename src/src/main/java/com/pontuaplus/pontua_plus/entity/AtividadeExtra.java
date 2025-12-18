package com.pontuaplus.pontua_plus.entity;

import com.pontuaplus.pontua_plus.enums.TipoAtividade;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "atividades_extras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtividadeExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @NotBlank(message = "Nome da atividade é obrigatório")
    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAtividade tipo;

    @Min(value = 1, message = "Bimestre mínimo é 1")
    @Max(value = 4, message = "Bimestre máximo é 4")
    @Column(nullable = false)
    private Integer bimestre;

    @Column(name = "pontos_conquistados")
    private Integer pontosConquistados;

    @PrePersist
    @PreUpdate
    private void calcularPontos() {
        if (tipo != null) {
            pontosConquistados = tipo.getPontos();
        }
    }
}
