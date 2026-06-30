package com.pontuaplus.pontua_plus.dto;

import com.pontuaplus.pontua_plus.entity.Aluno;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlunoResumoDTO {
    private Long id;
    private String nome;
    private String matricula;
    private String serie;
    private String turma;
    private String colegio;

    public static AlunoResumoDTO fromEntity(Aluno aluno) {
        return new AlunoResumoDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getMatricula(),
                aluno.getSerie(),
                aluno.getTurma(),
                aluno.getColegio()
        );
    }
}
