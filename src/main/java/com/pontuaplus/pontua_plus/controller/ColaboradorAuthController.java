package com.pontuaplus.pontua_plus.controller;

import com.pontuaplus.pontua_plus.entity.Colaborador;
import com.pontuaplus.pontua_plus.repository.ColaboradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/colaborador")
@RequiredArgsConstructor
public class ColaboradorAuthController {

    private final ColaboradorRepository colaboradorRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getColaboradorLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Colaborador colaborador = colaboradorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Colaborador não encontrado"));

        return ResponseEntity.ok(colaborador);
    }
}

