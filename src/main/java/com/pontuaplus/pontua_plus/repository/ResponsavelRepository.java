package com.pontuaplus.pontua_plus.repository;

import com.pontuaplus.pontua_plus.entity.Responsavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {
    Optional<Responsavel> findByEmail(String email);
    Optional<Responsavel> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
}
