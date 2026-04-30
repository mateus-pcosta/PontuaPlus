package com.pontuaplus.pontua_plus.service;

import com.pontuaplus.pontua_plus.dto.DashboardDTO;
import com.pontuaplus.pontua_plus.entity.*;
import com.pontuaplus.pontua_plus.exception.ResourceNotFoundException;
import com.pontuaplus.pontua_plus.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AlunoService alunoService;
    private final PontuacaoRepository pontuacaoRepository;
    private final NotaRepository notaRepository;
    private final FrequenciaRepository frequenciaRepository;
    private final AtividadeExtraRepository atividadeExtraRepository;

    @Transactional(readOnly = true)
    public DashboardDTO montarDashboard(String email) {
        Aluno aluno = alunoService.buscarPorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));

        int bimestre = aluno.getBimestreAtual() != null ? aluno.getBimestreAtual() : 2;

        Pontuacao pontuacao = pontuacaoRepository.findByAluno(aluno)
                .orElseGet(() -> {
                    Pontuacao nova = new Pontuacao();
                    nova.setAluno(aluno);
                    return pontuacaoRepository.save(nova);
                });

        List<Nota> notas             = notaRepository.findByAlunoAndBimestre(aluno, bimestre);
        List<Frequencia> frequencias = frequenciaRepository.findByAlunoAndBimestre(aluno, bimestre);
        List<AtividadeExtra> extras  = atividadeExtraRepository.findByAlunoAndBimestre(aluno, bimestre);

        return DashboardDTO.fromEntities(aluno, pontuacao, notas, frequencias, extras);
    }
}
