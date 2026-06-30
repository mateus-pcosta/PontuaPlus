package com.pontuaplus.pontua_plus.controller;

import com.pontuaplus.pontua_plus.dto.DiretorDashboardDTO;
import com.pontuaplus.pontua_plus.enums.TipoUsuario;
import com.pontuaplus.pontua_plus.repository.AlunoRepository;
import com.pontuaplus.pontua_plus.repository.ColaboradorRepository;
import com.pontuaplus.pontua_plus.repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diretor")
@RequiredArgsConstructor
public class DiretorController {

    private final AlunoRepository alunoRepository;
    private final ResponsavelRepository responsavelRepository;
    private final ColaboradorRepository colaboradorRepository;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('DIRETOR') or hasRole('DEV')")
    public ResponseEntity<DiretorDashboardDTO> getDashboard() {
        long equipeTotal = colaboradorRepository.countByTipo(TipoUsuario.PROFESSOR)
                + colaboradorRepository.countByTipo(TipoUsuario.ADMINISTRADOR)
                + colaboradorRepository.countByTipo(TipoUsuario.COORDENADOR)
                + colaboradorRepository.countByTipo(TipoUsuario.DIRETOR);
        DiretorDashboardDTO dto = new DiretorDashboardDTO(
                alunoRepository.count(),
                responsavelRepository.count(),
                colaboradorRepository.countByTipo(TipoUsuario.PROFESSOR),
                colaboradorRepository.countByTipo(TipoUsuario.ADMINISTRADOR),
                equipeTotal
        );
        return ResponseEntity.ok(dto);
    }
}
