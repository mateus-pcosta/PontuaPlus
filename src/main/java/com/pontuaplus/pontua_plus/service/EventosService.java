package com.pontuaplus.pontua_plus.service;

import com.pontuaplus.pontua_plus.dto.AtividadeExtraDetalhadaDTO;
import com.pontuaplus.pontua_plus.dto.SubmeterAtividadeDTO;
import com.pontuaplus.pontua_plus.entity.Aluno;
import com.pontuaplus.pontua_plus.entity.AtividadeExtra;
import com.pontuaplus.pontua_plus.exception.ResourceNotFoundException;
import com.pontuaplus.pontua_plus.repository.AtividadeExtraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventosService {

    private final AlunoService alunoService;
    private final AtividadeExtraRepository atividadeExtraRepository;

    @Transactional(readOnly = true)
    public List<AtividadeExtraDetalhadaDTO> listarAtividades(String email) {
        Aluno aluno = alunoService.buscarPorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));

        return atividadeExtraRepository.findByAluno(aluno).stream()
                .map(AtividadeExtraDetalhadaDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AtividadeExtraDetalhadaDTO submeter(String email, SubmeterAtividadeDTO dto) {
        Aluno aluno = alunoService.buscarPorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));

        String nome = (dto.getNome() != null && !dto.getNome().isBlank())
                ? dto.getNome()
                : dto.getTipo().getDescricao();

        int bimestre = aluno.getBimestreAtual() != null
                ? aluno.getBimestreAtual()
                : AlunoService.calcularBimestreAtual();

        AtividadeExtra atividade = new AtividadeExtra();
        atividade.setAluno(aluno);
        atividade.setTipo(dto.getTipo());
        atividade.setNome(nome);
        atividade.setBimestre(bimestre);

        return AtividadeExtraDetalhadaDTO.fromEntity(atividadeExtraRepository.save(atividade));
    }
}
