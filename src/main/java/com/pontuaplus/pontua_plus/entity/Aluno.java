package com.pontuaplus.pontua_plus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alunos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"notas", "frequencias", "atividadesExtras", "pontuacao"})
public class Aluno extends Usuario {

    @NotBlank(message = "Matrícula é obrigatória")
    @Column(nullable = false, unique = true)
    private String matricula;

    @NotBlank(message = "CPF é obrigatório")
    @Column(nullable = false, unique = true)
    private String cpf;

    @NotBlank(message = "Série é obrigatória")
    @Column(nullable = false)
    private String serie;

    @NotBlank(message = "Colégio é obrigatório")
    @Column(nullable = false)
    private String colegio;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "data_ingresso")
    private LocalDate dataIngresso;

    @Column
    private String turma;

    @Column(name = "bimestre_atual")
    private Integer bimestreAtual = 2;

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Nota> notas = new ArrayList<>();

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Frequencia> frequencias = new ArrayList<>();

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AtividadeExtra> atividadesExtras = new ArrayList<>();

    @OneToOne(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private Pontuacao pontuacao;
}
