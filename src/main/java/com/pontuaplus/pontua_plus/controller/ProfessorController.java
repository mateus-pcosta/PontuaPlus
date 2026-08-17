package com.pontuaplus.pontua_plus.controller;

import com.pontuaplus.pontua_plus.dto.ProfessorTurmaDTO;
import com.pontuaplus.pontua_plus.repository.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professor")
@RequiredArgsConstructor
public class ProfessorController {

    private final AlunoRepository alunoRepository;

    @GetMapping("/turmas")
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('DEV')")
    public ResponseEntity<List<ProfessorTurmaDTO>> getTurmas() {
        List<Object[]> rows = alunoRepository.findTurmasComContagem();
        List<ProfessorTurmaDTO> turmas = rows.stream()
                .map(r -> new ProfessorTurmaDTO((String) r[0], (String) r[1], (Long) r[2]))
                .toList();
        return ResponseEntity.ok(turmas);
    }
}
