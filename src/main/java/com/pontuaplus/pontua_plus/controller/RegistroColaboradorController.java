package com.pontuaplus.pontua_plus.controller;

import com.pontuaplus.pontua_plus.dto.RegistroColaboradorDTO;
import com.pontuaplus.pontua_plus.service.ColaboradorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/registro/colaborador")
@RequiredArgsConstructor
public class RegistroColaboradorController {

    private final ColaboradorService colaboradorService;

    @PostMapping
    public ResponseEntity<Map<String, String>> registrarColaborador(@Valid @RequestBody RegistroColaboradorDTO dto) {
        colaboradorService.registrar(dto);
        return ResponseEntity.ok(Map.of(
                "success", "Cadastro realizado com sucesso!",
                "message", "Você já pode fazer login com suas credenciais"
        ));
    }
}
