package com.pontuaplus.pontua_plus.dto;

import com.pontuaplus.pontua_plus.entity.AtividadeExtra;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtividadeExtraDetalhadaDTO {

    private Long id;
    private String nome;
    private String tipo;
    private Integer bimestre;
    private Integer pontosConquistados;

    public static AtividadeExtraDetalhadaDTO fromEntity(AtividadeExtra a) {
        return new AtividadeExtraDetalhadaDTO(
                a.getId(),
                a.getNome(),
                a.getTipo().getDescricao(),
                a.getBimestre(),
                a.getPontosConquistados()
        );
    }
}
