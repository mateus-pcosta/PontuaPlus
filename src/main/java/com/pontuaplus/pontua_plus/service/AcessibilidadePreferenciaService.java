package com.pontuaplus.pontua_plus.service;

import com.pontuaplus.pontua_plus.dto.AcessibilidadePreferenciaDTO;
import com.pontuaplus.pontua_plus.entity.AcessibilidadePreferencia;
import com.pontuaplus.pontua_plus.entity.Usuario;
import com.pontuaplus.pontua_plus.enums.TamanhoFonte;
import com.pontuaplus.pontua_plus.exception.ResourceNotFoundException;
import com.pontuaplus.pontua_plus.repository.AcessibilidadePreferenciaRepository;
import com.pontuaplus.pontua_plus.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AcessibilidadePreferenciaService {

    private final UsuarioRepository usuarioRepository;
    private final AcessibilidadePreferenciaRepository preferenciaRepository;

    @Transactional
    public AcessibilidadePreferenciaDTO buscar(String email) {
        return AcessibilidadePreferenciaDTO.fromEntity(buscarOuCriar(email));
    }

    @Transactional
    public AcessibilidadePreferenciaDTO salvar(String email, AcessibilidadePreferenciaDTO dto) {
        AcessibilidadePreferencia preferencia = buscarOuCriar(email);
        preferencia.setTamanhoFonte(dto.getTamanhoFonte() != null ? dto.getTamanhoFonte() : TamanhoFonte.MEDIA);
        preferencia.setAltoContraste(Boolean.TRUE.equals(dto.getAltoContraste()));
        preferencia.setReduzMovimento(Boolean.TRUE.equals(dto.getReduzMovimento()));
        preferencia.setSublinharLinks(Boolean.TRUE.equals(dto.getSublinharLinks()));
        preferencia.setLibras(Boolean.TRUE.equals(dto.getLibras()));
        preferencia.setLeituraVoz(Boolean.TRUE.equals(dto.getLeituraVoz()));
        return AcessibilidadePreferenciaDTO.fromEntity(preferenciaRepository.save(preferencia));
    }

    private AcessibilidadePreferencia buscarOuCriar(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return preferenciaRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    AcessibilidadePreferencia nova = new AcessibilidadePreferencia();
                    nova.setUsuario(usuario);
                    return preferenciaRepository.save(nova);
                });
    }
}
