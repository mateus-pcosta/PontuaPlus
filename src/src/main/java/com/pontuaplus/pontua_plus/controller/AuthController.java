package com.pontuaplus.pontua_plus.controller;

import com.pontuaplus.pontua_plus.dto.AlunoDTO;
import com.pontuaplus.pontua_plus.entity.Aluno;
import com.pontuaplus.pontua_plus.service.AlunoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AlunoService alunoService;

    @GetMapping("/me")
    public ResponseEntity<AlunoDTO> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Aluno aluno = alunoService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        return ResponseEntity.ok(AlunoDTO.fromEntity(aluno));
    }
}
