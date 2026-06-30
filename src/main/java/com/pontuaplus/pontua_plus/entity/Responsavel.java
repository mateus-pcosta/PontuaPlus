package com.pontuaplus.pontua_plus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "responsaveis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = "filhos")
public class Responsavel extends Usuario {

    @NotBlank(message = "CPF é obrigatório")
    @Column(nullable = false, unique = true)
    private String cpf;

    @Column
    private String telefone;

    @ManyToMany
    @JoinTable(
        name = "responsaveis_alunos",
        joinColumns = @JoinColumn(name = "responsavel_id"),
        inverseJoinColumns = @JoinColumn(name = "aluno_id")
    )
    private List<Aluno> filhos = new ArrayList<>();
}
