package com.pontuaplus.pontua_plus.controller;

import com.pontuaplus.pontua_plus.dto.DashboardDTO;
import com.pontuaplus.pontua_plus.entity.*;
import com.pontuaplus.pontua_plus.repository.*;
import com.pontuaplus.pontua_plus.service.AlunoService;
import com.pontuaplus.pontua_plus.service.PontuacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AlunoService alunoService;
    private final PontuacaoService pontuacaoService;
    private final NotaRepository notaRepository;
    private final FrequenciaRepository frequenciaRepository;
    private final AtividadeExtraRepository atividadeExtraRepository;

    @GetMapping
    public ResponseEntity<DashboardDTO> getDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Aluno aluno = alunoService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        // Buscar ou calcular pontuação
        Pontuacao pontuacao = pontuacaoService.calcularPontuacaoAluno(aluno);
        pontuacaoService.atualizarRankings();

        // Buscar dados relacionados
        List<Nota> notas = notaRepository.findByAluno(aluno);
        List<Frequencia> frequencias = frequenciaRepository.findByAluno(aluno);
        List<AtividadeExtra> atividades = atividadeExtraRepository.findByAluno(aluno);

        DashboardDTO dashboard = DashboardDTO.fromEntities(aluno, pontuacao, notas, frequencias, atividades);

        return ResponseEntity.ok(dashboard);
    }
}
