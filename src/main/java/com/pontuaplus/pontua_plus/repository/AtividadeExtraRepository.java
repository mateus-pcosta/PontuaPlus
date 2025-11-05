package com.pontuaplus.pontua_plus.repository;

import com.pontuaplus.pontua_plus.entity.Aluno;
import com.pontuaplus.pontua_plus.entity.AtividadeExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtividadeExtraRepository extends JpaRepository<AtividadeExtra, Long> {
    List<AtividadeExtra> findByAluno(Aluno aluno);
    List<AtividadeExtra> findByAlunoId(Long alunoId);
}
