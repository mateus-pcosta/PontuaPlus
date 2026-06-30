package com.pontuaplus.pontua_plus.dto;

import com.pontuaplus.pontua_plus.enums.TipoAtividade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoAtividadeDTO {

    private String key;
    private String descricao;
    private int pontos;

    public static TipoAtividadeDTO fromEnum(TipoAtividade tipo) {
        return new TipoAtividadeDTO(tipo.name(), tipo.getDescricao(), tipo.getPontos());
    }
}
