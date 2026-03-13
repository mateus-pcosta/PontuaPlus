package com.pontuaplus.pontua_plus.dto;

import com.pontuaplus.pontua_plus.entity.Aluno;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlunoDTO {
    private Long id;
    private String nome;
    private String email;
    private String matricula;
    private String serie;
    private String colegio;
    private LocalDate dataNascimento;
    private LocalDate dataIngresso;
    private String turma;

    public static AlunoDTO fromEntity(Aluno aluno) {
        return new AlunoDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getMatricula(),
                aluno.getSerie(),
                aluno.getColegio(),
                aluno.getDataNascimento(),
                aluno.getDataIngresso(),
                aluno.getTurma()
        );
    }
}
