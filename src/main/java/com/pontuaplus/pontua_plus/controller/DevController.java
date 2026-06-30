package com.pontuaplus.pontua_plus.controller;

import com.pontuaplus.pontua_plus.dto.DevStatsDTO;
import com.pontuaplus.pontua_plus.enums.TipoUsuario;
import com.pontuaplus.pontua_plus.repository.AlunoRepository;
import com.pontuaplus.pontua_plus.repository.ColaboradorRepository;
import com.pontuaplus.pontua_plus.repository.ResponsavelRepository;
import com.pontuaplus.pontua_plus.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevController {

    private final AlunoRepository alunoRepository;
    private final ResponsavelRepository responsavelRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<DevStatsDTO> getStats() {
        DevStatsDTO dto = new DevStatsDTO(
                alunoRepository.count(),
                responsavelRepository.count(),
                colaboradorRepository.countByTipo(TipoUsuario.PROFESSOR),
                colaboradorRepository.countByTipo(TipoUsuario.ADMINISTRADOR),
                colaboradorRepository.countByTipo(TipoUsuario.DIRETOR),
                colaboradorRepository.countByTipo(TipoUsuario.DEV),
                usuarioRepository.count()
        );
        return ResponseEntity.ok(dto);
    }
}
