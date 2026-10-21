package com.pontuaplus.pontua_plus.controller;

import com.pontuaplus.pontua_plus.dto.AcessibilidadePreferenciaDTO;
import com.pontuaplus.pontua_plus.service.AcessibilidadePreferenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/acessibilidade")
@RequiredArgsConstructor
public class AcessibilidadeController {

    private final AcessibilidadePreferenciaService preferenciaService;

    @GetMapping
    public ResponseEntity<AcessibilidadePreferenciaDTO> buscar() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(preferenciaService.buscar(auth.getName()));
    }

    @PutMapping
    public ResponseEntity<AcessibilidadePreferenciaDTO> salvar(@RequestBody AcessibilidadePreferenciaDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(preferenciaService.salvar(auth.getName(), dto));
    }
}
