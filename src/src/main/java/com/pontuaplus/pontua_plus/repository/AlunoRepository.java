package com.pontuaplus.pontua_plus.repository;

import com.pontuaplus.pontua_plus.entity.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Optional<Aluno> findByMatricula(String matricula);
    Optional<Aluno> findByEmail(String email);
    boolean existsByMatricula(String matricula);
}
