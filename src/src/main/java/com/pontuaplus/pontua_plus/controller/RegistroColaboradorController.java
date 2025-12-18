package com.pontuaplus.pontua_plus.controller;

import com.pontuaplus.pontua_plus.dto.RegistroColaboradorDTO;
import com.pontuaplus.pontua_plus.entity.Colaborador;
import com.pontuaplus.pontua_plus.repository.ColaboradorRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/registro/colaborador")
@RequiredArgsConstructor
public class RegistroColaboradorController {

    private final PasswordEncoder passwordEncoder;
    private final ColaboradorRepository colaboradorRepository;
    

    @PostMapping
    public ResponseEntity<?> registrarAluno(@Valid @RequestBody RegistroColaboradorDTO dto) {
        Map<String, String> response = new HashMap<>();

        // Verificar se e-mail já existe
        if (colaboradorRepository.findByEmail(dto.getEmail()).isPresent()) {
            response.put("error", "E-mail já cadastrado");
            return ResponseEntity.badRequest().body(response);
        }

        // Verificar se matrícula já existe
        if (colaboradorRepository.existsByMatricula(dto.getMatricula())) {
            response.put("error", "Matrícula já cadastrada");
            return ResponseEntity.badRequest().body(response);
        }

        // Criar novo colaborador
        Colaborador colaborador = new Colaborador();
        colaborador.setNome(dto.getNome());
        colaborador.setEmail(dto.getEmail());
        colaborador.setSenha(passwordEncoder.encode(dto.getSenha()));
        colaborador.setTipo(dto.getTipo());
        colaborador.setMatricula(dto.getMatricula());
        colaborador.setCpf(dto.getCpf());
        colaborador.setColegio(dto.getColegio());
        colaborador.setDataNascimento(dto.getDataNascimento());
        colaborador.setDataIngresso(LocalDate.now());

        colaboradorRepository.save(colaborador);
        
        response.put("success", "Cadastro realizado com sucesso!");
        response.put("message", "Você já pode fazer login com suas credenciais");
        return ResponseEntity.ok(response);
    }
}
