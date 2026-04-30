package com.pontuaplus.pontua_plus.service;

import com.pontuaplus.pontua_plus.dto.RegistroAlunoDTO;
import com.pontuaplus.pontua_plus.entity.Aluno;
import com.pontuaplus.pontua_plus.enums.TipoUsuario;
import com.pontuaplus.pontua_plus.exception.ConflictException;
import com.pontuaplus.pontua_plus.repository.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<Aluno> listarTodos() {
        return alunoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Aluno> buscarPorId(Long id) {
        return alunoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Aluno> buscarPorEmail(String email) {
        return alunoRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<Aluno> buscarPorMatricula(String matricula) {
        return alunoRepository.findByMatricula(matricula);
    }

    @Transactional
    public Aluno registrar(RegistroAlunoDTO dto) {
        if (alunoRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException("E-mail já cadastrado");
        }
        if (alunoRepository.existsByMatricula(dto.getMatricula())) {
            throw new ConflictException("Matrícula já cadastrada");
        }

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
        aluno.setBimestreAtual(calcularBimestreAtual());

        return alunoRepository.save(aluno);
    }

    @Transactional
    public Aluno salvar(Aluno aluno) {
        return alunoRepository.save(aluno);
    }

    @Transactional
    public void deletar(Long id) {
        alunoRepository.deleteById(id);
    }

    public static int calcularBimestreAtual() {
        int mes = LocalDate.now().getMonthValue();
        if (mes <= 3) return 1;
        if (mes <= 6) return 2;
        if (mes <= 9) return 3;
        return 4;
    }
}
