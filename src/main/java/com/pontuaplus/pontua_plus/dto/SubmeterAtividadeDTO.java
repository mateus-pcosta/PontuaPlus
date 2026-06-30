package com.pontuaplus.pontua_plus.dto;

import com.pontuaplus.pontua_plus.enums.TipoAtividade;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmeterAtividadeDTO {

    @NotNull(message = "Tipo de atividade é obrigatório")
    private TipoAtividade tipo;

    private String nome;
}
