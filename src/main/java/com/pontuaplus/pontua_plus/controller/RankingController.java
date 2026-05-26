package com.pontuaplus.pontua_plus.controller;

import com.pontuaplus.pontua_plus.dto.RankingDTO;
import com.pontuaplus.pontua_plus.service.PontuacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final PontuacaoService pontuacaoService;

    @GetMapping
    public ResponseEntity<RankingDTO> getRanking(Authentication authentication) {
        return ResponseEntity.ok(pontuacaoService.montarRanking(authentication.getName()));
    }
}
