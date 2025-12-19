package com.pontuaplus.pontua_plus.service;

import com.pontuaplus.pontua_plus.entity.Colaborador;
import com.pontuaplus.pontua_plus.repository.ColaboradorRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ColaboradorService {

    private final ColaboradorRepository colaboradorRepository;

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
    public Colaborador salvar(Colaborador colaborador) {
        return colaboradorRepository.save(colaborador);
    }

    @Transactional
    public void deletar(Long id) {
        colaboradorRepository.deleteById(id);
    }
}