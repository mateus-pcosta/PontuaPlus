package com.pontuaplus.pontua_plus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "colaboradores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
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

    /*@Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoUsuario tipo;*/    
}

