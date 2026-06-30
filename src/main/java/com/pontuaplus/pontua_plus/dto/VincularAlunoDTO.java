package com.pontuaplus.pontua_plus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VincularAlunoDTO {
    @NotBlank(message = "Matrícula é obrigatória")
    private String matricula;
}
