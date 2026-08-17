package com.pontuaplus.pontua_plus.repository;

import com.pontuaplus.pontua_plus.entity.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Optional<Aluno> findByMatricula(String matricula);
    Optional<Aluno> findByEmail(String email);
    boolean existsByMatricula(String matricula);

    @Query("SELECT a.turma, a.serie, COUNT(a) FROM Aluno a GROUP BY a.turma, a.serie ORDER BY a.serie, a.turma")
    List<Object[]> findTurmasComContagem();
}
