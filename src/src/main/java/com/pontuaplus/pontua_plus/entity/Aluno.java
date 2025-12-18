package com.pontuaplus.pontua_plus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alunos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
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
    private Integer bimestreAtual = 2; // Bimestre padrão atual

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Nota> notas = new ArrayList<>();

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Frequencia> frequencias = new ArrayList<>();

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AtividadeExtra> atividadesExtras = new ArrayList<>();

    @OneToOne(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private Pontuacao pontuacao;
}
