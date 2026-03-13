package com.pontuaplus.pontua_plus.repository;

import com.pontuaplus.pontua_plus.entity.Aluno;
import com.pontuaplus.pontua_plus.entity.Pontuacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PontuacaoRepository extends JpaRepository<Pontuacao, Long> {
    Optional<Pontuacao> findByAluno(Aluno aluno);
    Optional<Pontuacao> findByAlunoId(Long alunoId);

    @Query("SELECT p FROM Pontuacao p ORDER BY p.totalPontos DESC")
    List<Pontuacao> findAllOrderByTotalPontosDesc();
}
