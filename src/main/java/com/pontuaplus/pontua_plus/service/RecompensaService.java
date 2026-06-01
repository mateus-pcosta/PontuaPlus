package com.pontuaplus.pontua_plus.service;

import com.pontuaplus.pontua_plus.dto.RecompensaDTO;
import com.pontuaplus.pontua_plus.dto.RecompensaDTO.*;
import com.pontuaplus.pontua_plus.entity.Aluno;
import com.pontuaplus.pontua_plus.entity.Pontuacao;
import com.pontuaplus.pontua_plus.enums.Ranking;
import com.pontuaplus.pontua_plus.exception.ResourceNotFoundException;
import com.pontuaplus.pontua_plus.repository.EmblemaDigitalRepository;
import com.pontuaplus.pontua_plus.repository.PontuacaoRepository;
import com.pontuaplus.pontua_plus.repository.RecompensaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecompensaService {

    private final AlunoService alunoService;
    private final PontuacaoRepository pontuacaoRepository;
    private final RecompensaRepository recompensaRepository;
    private final EmblemaDigitalRepository emblemaDigitalRepository;

    @Transactional(readOnly = true)
    public RecompensaDTO montarRecompensas(String email) {
        Aluno aluno = alunoService.buscarPorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));

        Pontuacao pontuacao = pontuacaoRepository.findByAluno(aluno)
                .orElseThrow(() -> new ResourceNotFoundException("Pontuação não encontrada"));

        Ranking rankingAtual = pontuacao.getRanking();
        Ranking proximoRanking = proximoTier(rankingAtual);

        int pontosParaProximo = proximoRanking != null
                ? proximoRanking.getPontoMinimo() - pontuacao.getTotalPontos()
                : 0;

        StatusAtualDTO status = new StatusAtualDTO(
                pontuacao.getTotalPontos(),
                rankingAtual.name(),
                proximoRanking != null ? proximoRanking.name() : null,
                Math.max(0, pontosParaProximo)
        );

        List<TierRecompensasDTO> tiers = Arrays.stream(Ranking.values())
                .sorted((a, b) -> b.getPontoMinimo() - a.getPontoMinimo())
                .map(tier -> {
                    List<RecompensaItemDTO> itens = recompensaRepository
                            .findByRankingMinimoAndAtivo(tier, true)
                            .stream()
                            .map(r -> new RecompensaItemDTO(r.getId(), r.getNome(), r.getDescricao(), r.getParceiro(), r.getIcone()))
                            .toList();
                    return new TierRecompensasDTO(tier.name(), tier.getPontoMinimo(), tier == rankingAtual, itens);
                })
                .toList();

        List<EmblemaDTO> emblemas = emblemaDigitalRepository
                .findByAlunoOrderByAnoDescBimestreDesc(aluno)
                .stream()
                .map(e -> new EmblemaDTO(e.getTitulo(), e.getRanking().name(), e.getBimestre(), e.getAno()))
                .toList();

        return new RecompensaDTO(status, tiers, emblemas);
    }

    @Transactional(readOnly = true)
    public List<EmblemaDTO> listarEmblemas(String email) {
        Aluno aluno = alunoService.buscarPorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));

        return emblemaDigitalRepository
                .findByAlunoOrderByAnoDescBimestreDesc(aluno)
                .stream()
                .map(e -> new EmblemaDTO(e.getTitulo(), e.getRanking().name(), e.getBimestre(), e.getAno()))
                .toList();
    }

    private Ranking proximoTier(Ranking atual) {
        return switch (atual) {
            case BRONZE  -> Ranking.PRATA;
            case PRATA   -> Ranking.OURO;
            case OURO    -> Ranking.DIAMOND;
            case DIAMOND -> null;
        };
    }
}
