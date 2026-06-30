package com.pontuaplus.pontua_plus.controller;

import com.pontuaplus.pontua_plus.dto.AlunoResumoDTO;
import com.pontuaplus.pontua_plus.dto.VincularAlunoDTO;
import com.pontuaplus.pontua_plus.entity.Responsavel;
import com.pontuaplus.pontua_plus.exception.ResourceNotFoundException;
import com.pontuaplus.pontua_plus.repository.ResponsavelRepository;
import com.pontuaplus.pontua_plus.service.ResponsavelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/responsavel")
@RequiredArgsConstructor
public class ResponsavelAuthController {

    private final ResponsavelService responsavelService;
    private final ResponsavelRepository responsavelRepository;

    @GetMapping("/me")
    @PreAuthorize("hasRole('RESPONSAVEL') or hasRole('DEV')")
    public ResponseEntity<Map<String, String>> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Responsavel responsavel = responsavelRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado"));
        return ResponseEntity.ok(Map.of("nome", responsavel.getNome()));
    }

    @GetMapping("/filhos")
    @PreAuthorize("hasRole('RESPONSAVEL') or hasRole('DEV')")
    public ResponseEntity<List<AlunoResumoDTO>> listarFilhos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(responsavelService.listarFilhos(auth.getName()));
    }

    @PostMapping("/vincular")
    @PreAuthorize("hasRole('RESPONSAVEL') or hasRole('DEV')")
    public ResponseEntity<Map<String, String>> vincularAluno(@Valid @RequestBody VincularAlunoDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        responsavelService.vincularAluno(auth.getName(), dto.getMatricula());
        return ResponseEntity.ok(Map.of("success", "Aluno vinculado com sucesso!"));
    }
}
