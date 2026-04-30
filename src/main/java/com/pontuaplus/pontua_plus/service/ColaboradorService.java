package com.pontuaplus.pontua_plus.service;

import com.pontuaplus.pontua_plus.dto.RegistroColaboradorDTO;
import com.pontuaplus.pontua_plus.entity.Colaborador;
import com.pontuaplus.pontua_plus.exception.ConflictException;
import com.pontuaplus.pontua_plus.repository.ColaboradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ColaboradorService {

    private final ColaboradorRepository colaboradorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<Colaborador> listarTodos() {
        return colaboradorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Colaborador> buscarPorId(Long id) {
        return colaboradorRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Colaborador> buscarPorEmail(String email) {
        return colaboradorRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<Colaborador> buscarPorMatricula(String matricula) {
        return colaboradorRepository.findByMatricula(matricula);
    }

    @Transactional
    public Colaborador registrar(RegistroColaboradorDTO dto) {
        if (colaboradorRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException("E-mail já cadastrado");
        }
        if (colaboradorRepository.existsByMatricula(dto.getMatricula())) {
            throw new ConflictException("Matrícula já cadastrada");
        }

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

        return colaboradorRepository.save(colaborador);
    }

    @Transactional
    public Colaborador salvar(Colaborador colaborador) {
        return colaboradorRepository.save(colaborador);
    }

    @Transactional
    public void deletar(Long id) {
        colaboradorRepository.deleteById(id);
    }
}
