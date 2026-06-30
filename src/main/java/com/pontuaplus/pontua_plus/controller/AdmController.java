package com.pontuaplus.pontua_plus.controller;

import com.pontuaplus.pontua_plus.dto.AdmDashboardDTO;
import com.pontuaplus.pontua_plus.enums.TipoUsuario;
import com.pontuaplus.pontua_plus.repository.AlunoRepository;
import com.pontuaplus.pontua_plus.repository.ResponsavelRepository;
import com.pontuaplus.pontua_plus.repository.ColaboradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/adm")
@RequiredArgsConstructor
public class AdmController {

    private final AlunoRepository alunoRepository;
    private final ResponsavelRepository responsavelRepository;
    private final ColaboradorRepository colaboradorRepository;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DEV')")
    public ResponseEntity<AdmDashboardDTO> getDashboard() {
        AdmDashboardDTO dto = new AdmDashboardDTO(
                alunoRepository.count(),
                responsavelRepository.count(),
                colaboradorRepository.countByTipo(TipoUsuario.PROFESSOR),
                colaboradorRepository.countByTipo(TipoUsuario.ADMINISTRADOR),
                colaboradorRepository.countByTipo(TipoUsuario.DIRETOR)
        );
        return ResponseEntity.ok(dto);
    }
}
