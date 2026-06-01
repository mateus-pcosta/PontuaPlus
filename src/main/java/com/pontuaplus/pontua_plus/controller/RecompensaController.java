package com.pontuaplus.pontua_plus.controller;

import com.pontuaplus.pontua_plus.dto.RecompensaDTO;
import com.pontuaplus.pontua_plus.service.RecompensaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recompensas")
@RequiredArgsConstructor
public class RecompensaController {

    private final RecompensaService recompensaService;

    @GetMapping
    public ResponseEntity<RecompensaDTO> getRecompensas(Authentication authentication) {
        return ResponseEntity.ok(recompensaService.montarRecompensas(authentication.getName()));
    }

    @GetMapping("/emblemas")
    public ResponseEntity<List<RecompensaDTO.EmblemaDTO>> getEmblemas(Authentication authentication) {
        return ResponseEntity.ok(recompensaService.listarEmblemas(authentication.getName()));
    }
}
