package com.pontuaplus.pontua_plus.repository;

import com.pontuaplus.pontua_plus.entity.AcessibilidadePreferencia;
import com.pontuaplus.pontua_plus.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcessibilidadePreferenciaRepository extends JpaRepository<AcessibilidadePreferencia, Long> {
    Optional<AcessibilidadePreferencia> findByUsuario(Usuario usuario);
}
