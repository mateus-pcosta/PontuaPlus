package com.pontuaplus.pontua_plus.controller;

import com.pontuaplus.pontua_plus.dto.RegistroResponsavelDTO;
import com.pontuaplus.pontua_plus.service.ResponsavelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/registro/responsavel")
@RequiredArgsConstructor
public class RegistroResponsavelController {

    private final ResponsavelService responsavelService;

    @PostMapping
    public ResponseEntity<Map<String, String>> registrarResponsavel(@Valid @RequestBody RegistroResponsavelDTO dto) {
        responsavelService.registrar(dto);
        return ResponseEntity.ok(Map.of(
                "success", "Cadastro realizado com sucesso!",
                "message", "Você já pode fazer login com suas credenciais"
        ));
    }
}
