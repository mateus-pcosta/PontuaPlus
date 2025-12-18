package com.pontuaplus.pontua_plus.repository;

import com.pontuaplus.pontua_plus.entity.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {
    Optional<Colaborador> findByMatricula(String matricula);
    Optional<Colaborador> findByEmail(String email);
    boolean existsByMatricula(String matricula);
    
}
