package com.pontuaplus.pontua_plus.dto;

import com.pontuaplus.pontua_plus.entity.AcessibilidadePreferencia;
import com.pontuaplus.pontua_plus.enums.TamanhoFonte;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcessibilidadePreferenciaDTO {
    private TamanhoFonte tamanhoFonte;
    private Boolean altoContraste;
    private Boolean reduzMovimento;
    private Boolean sublinharLinks;
    private Boolean libras;
    private Boolean leituraVoz;

    public static AcessibilidadePreferenciaDTO fromEntity(AcessibilidadePreferencia preferencia) {
        return new AcessibilidadePreferenciaDTO(
                preferencia.getTamanhoFonte(),
                preferencia.getAltoContraste(),
                preferencia.getReduzMovimento(),
                preferencia.getSublinharLinks(),
                preferencia.getLibras(),
                preferencia.getLeituraVoz()
        );
    }
}
