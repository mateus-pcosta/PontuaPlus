package com.pontuaplus.pontua_plus.repository;

import com.pontuaplus.pontua_plus.entity.Aluno;
import com.pontuaplus.pontua_plus.entity.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByAluno(Aluno aluno);
    List<Nota> findByAlunoId(Long alunoId);
}
