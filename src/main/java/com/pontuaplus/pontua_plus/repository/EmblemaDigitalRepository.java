package com.pontuaplus.pontua_plus.repository;

import com.pontuaplus.pontua_plus.entity.Aluno;
import com.pontuaplus.pontua_plus.entity.EmblemaDigital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmblemaDigitalRepository extends JpaRepository<EmblemaDigital, Long> {
    List<EmblemaDigital> findByAlunoOrderByAnoDescBimestreDesc(Aluno aluno);
    boolean existsByAlunoAndBimestreAndAno(Aluno aluno, Integer bimestre, Integer ano);
}
