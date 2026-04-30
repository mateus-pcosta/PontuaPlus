package com.pontuaplus.pontua_plus.controller;

import com.pontuaplus.pontua_plus.dto.RegistroAlunoDTO;
import com.pontuaplus.pontua_plus.service.AlunoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/registro")
@RequiredArgsConstructor
public class RegistroController {

    private final AlunoService alunoService;

    @PostMapping
    public ResponseEntity<Map<String, String>> registrarAluno(@Valid @RequestBody RegistroAlunoDTO dto) {
        alunoService.registrar(dto);
        return ResponseEntity.ok(Map.of(
                "success", "Cadastro realizado com sucesso!",
                "message", "Você já pode fazer login com suas credenciais"
        ));
    }
}
