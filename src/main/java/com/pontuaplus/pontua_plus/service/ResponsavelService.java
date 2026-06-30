package com.pontuaplus.pontua_plus.service;

import com.pontuaplus.pontua_plus.dto.AlunoResumoDTO;
import com.pontuaplus.pontua_plus.dto.RegistroResponsavelDTO;
import com.pontuaplus.pontua_plus.entity.Aluno;
import com.pontuaplus.pontua_plus.entity.Responsavel;
import com.pontuaplus.pontua_plus.enums.TipoUsuario;
import com.pontuaplus.pontua_plus.exception.ConflictException;
import com.pontuaplus.pontua_plus.exception.ResourceNotFoundException;
import com.pontuaplus.pontua_plus.repository.AlunoRepository;
import com.pontuaplus.pontua_plus.repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResponsavelService {

    private final ResponsavelRepository responsavelRepository;
    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Optional<Responsavel> buscarPorEmail(String email) {
        return responsavelRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<AlunoResumoDTO> listarFilhos(String email) {
        Responsavel responsavel = responsavelRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado"));
        return responsavel.getFilhos().stream()
                .map(AlunoResumoDTO::fromEntity)
                .toList();
    }

    @Transactional
    public Responsavel registrar(RegistroResponsavelDTO dto) {
        if (responsavelRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException("E-mail já cadastrado");
        }
        if (responsavelRepository.existsByCpf(dto.getCpf())) {
            throw new ConflictException("CPF já cadastrado");
        }

        Responsavel responsavel = new Responsavel();
        responsavel.setNome(dto.getNome());
        responsavel.setEmail(dto.getEmail());
        responsavel.setSenha(passwordEncoder.encode(dto.getSenha()));
        responsavel.setTipo(TipoUsuario.RESPONSAVEL);
        responsavel.setCpf(dto.getCpf());
        responsavel.setTelefone(dto.getTelefone());

        return responsavelRepository.save(responsavel);
    }

    @Transactional
    public void vincularAluno(String emailResponsavel, String matricula) {
        Responsavel responsavel = responsavelRepository.findByEmail(emailResponsavel)
                .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado"));

        Aluno aluno = alunoRepository.findByMatricula(matricula)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com essa matrícula"));

        if (responsavel.getFilhos().contains(aluno)) {
            throw new ConflictException("Aluno já está vinculado a este responsável");
        }

        responsavel.getFilhos().add(aluno);
        responsavelRepository.save(responsavel);
    }
}
