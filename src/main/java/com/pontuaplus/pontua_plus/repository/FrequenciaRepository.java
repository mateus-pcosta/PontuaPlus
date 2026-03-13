package com.pontuaplus.pontua_plus.repository;

import com.pontuaplus.pontua_plus.entity.Aluno;
import com.pontuaplus.pontua_plus.entity.Frequencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FrequenciaRepository extends JpaRepository<Frequencia, Long> {
    List<Frequencia> findByAluno(Aluno aluno);
    List<Frequencia> findByAlunoId(Long alunoId);
}
