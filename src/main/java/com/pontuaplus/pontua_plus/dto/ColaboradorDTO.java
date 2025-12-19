package com.pontuaplus.pontua_plus.dto;

import com.pontuaplus.pontua_plus.entity.Colaborador;
import com.pontuaplus.pontua_plus.enums.TipoUsuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColaboradorDTO {
    private Long id;
    private String nome;
    private String email;
    private String matricula;
    private String colegio;
    private LocalDate dataNascimento;
    private LocalDate dataIngresso;
    private TipoUsuario tipo;

    public static ColaboradorDTO fromEntity(Colaborador colaborador) {
        return new ColaboradorDTO(
                colaborador.getId(),
                colaborador.getNome(),
                colaborador.getEmail(),
                colaborador.getMatricula(),
                colaborador.getColegio(),
                colaborador.getDataNascimento(),
                colaborador.getDataIngresso(),
                colaborador.getTipo()
        );
    }
}

