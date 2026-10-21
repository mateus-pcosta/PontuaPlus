package com.pontuaplus.pontua_plus.repository;

import com.pontuaplus.pontua_plus.entity.Aluno;
import com.pontuaplus.pontua_plus.entity.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByAluno(Aluno aluno);
    List<Nota> findByAlunoId(Long alunoId);
    List<Nota> findByAlunoAndBimestre(Aluno aluno, Integer bimestre);

    @Query("SELECT n.bimestre, AVG(n.valor) FROM Nota n " +
            "WHERE n.aluno.turma = :turma AND n.aluno.colegio = :colegio " +
            "GROUP BY n.bimestre")
    List<Object[]> mediaPorBimestreDaTurma(@Param("turma") String turma, @Param("colegio") String colegio);
}
