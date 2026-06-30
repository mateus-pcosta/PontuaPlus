package com.pontuaplus.pontua_plus.controller;

import com.pontuaplus.pontua_plus.dto.AtividadeExtraDetalhadaDTO;
import com.pontuaplus.pontua_plus.dto.SubmeterAtividadeDTO;
import com.pontuaplus.pontua_plus.dto.TipoAtividadeDTO;
import com.pontuaplus.pontua_plus.enums.TipoAtividade;
import com.pontuaplus.pontua_plus.service.EventosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventosController {

    private final EventosService eventosService;

    @GetMapping
    @PreAuthorize("hasRole('ALUNO') or hasRole('DEV')")
    public ResponseEntity<List<AtividadeExtraDetalhadaDTO>> listar() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(eventosService.listarAtividades(auth.getName()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ALUNO') or hasRole('DEV')")
    public ResponseEntity<AtividadeExtraDetalhadaDTO> submeter(@Valid @RequestBody SubmeterAtividadeDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(eventosService.submeter(auth.getName(), dto));
    }

    @GetMapping("/tipos")
    public ResponseEntity<List<TipoAtividadeDTO>> listarTipos() {
        List<TipoAtividadeDTO> tipos = Arrays.stream(TipoAtividade.values())
                .map(TipoAtividadeDTO::fromEnum)
                .toList();
        return ResponseEntity.ok(tipos);
    }
}
