package com.pontuaplus.pontua_plus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "colaboradores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Colaborador extends Usuario {

    @NotBlank(message = "Matrícula é obrigatória")
    @Column(nullable = false, unique = true)
    private String matricula;

    @NotBlank(message = "CPF é obrigatório")
    @Column(nullable = false, unique = true)
    private String cpf;

    @NotBlank(message = "Colégio é obrigatório")
    @Column(nullable = false)
    private String colegio;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "data_ingresso")
    private LocalDate dataIngresso;
}
