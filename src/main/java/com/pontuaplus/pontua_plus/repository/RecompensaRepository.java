package com.pontuaplus.pontua_plus.repository;

import com.pontuaplus.pontua_plus.entity.Recompensa;
import com.pontuaplus.pontua_plus.enums.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecompensaRepository extends JpaRepository<Recompensa, Long> {
    List<Recompensa> findByRankingMinimoAndAtivo(Ranking rankingMinimo, Boolean ativo);
}
