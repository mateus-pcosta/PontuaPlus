package com.pontuaplus.pontua_plus.repository;

import com.pontuaplus.pontua_plus.entity.Usuario;
import com.pontuaplus.pontua_plus.enums.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByTipo(TipoUsuario tipo);
}
