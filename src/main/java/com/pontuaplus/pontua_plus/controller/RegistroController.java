package com.pontuaplus.pontua_plus.controller;

import com.pontuaplus.pontua_plus.dto.RegistroAlunoDTO;
import com.pontuaplus.pontua_plus.entity.Aluno;
import com.pontuaplus.pontua_plus.enums.TipoUsuario;
import com.pontuaplus.pontua_plus.repository.AlunoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/registro")
@RequiredArgsConstructor
public class RegistroController {

    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> registrarAluno(@Valid @RequestBody RegistroAlunoDTO dto) {
        Map<String, String> response = new HashMap<>();

        // Verificar se e-mail já existe
        if (alunoRepository.findByEmail(dto.getEmail()).isPresent()) {
            response.put("error", "E-mail já cadastrado");
            return ResponseEntity.badRequest().body(response);
        }

        // Verificar se matrícula já existe
        if (alunoRepository.existsByMatricula(dto.getMatricula())) {
            response.put("error", "Matrícula já cadastrada");
            return ResponseEntity.badRequest().body(response);
        }

        // Criar novo aluno
        Aluno aluno = new Aluno();
        aluno.setNome(dto.getNome());
        aluno.setEmail(dto.getEmail());
        aluno.setSenha(passwordEncoder.encode(dto.getSenha()));
        aluno.setTipo(TipoUsuario.ALUNO);
        aluno.setMatricula(dto.getMatricula());
        aluno.setCpf(dto.getCpf());
        aluno.setSerie(dto.getSerie());
        aluno.setColegio(dto.getColegio());
        aluno.setDataNascimento(dto.getDataNascimento());
        aluno.setDataIngresso(LocalDate.now());
        aluno.setTurma(dto.getTurma());
        aluno.setBimestreAtual(2); // Bimestre atual padrão

        alunoRepository.save(aluno);

        response.put("success", "Cadastro realizado com sucesso!");
        response.put("message", "Você já pode fazer login com suas credenciais");
        return ResponseEntity.ok(response);
    }
}
