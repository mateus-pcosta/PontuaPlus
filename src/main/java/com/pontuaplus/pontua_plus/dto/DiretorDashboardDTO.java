package com.pontuaplus.pontua_plus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiretorDashboardDTO {
    private long totalAlunos;
    private long totalResponsaveis;
    private long totalProfessores;
    private long totalAdministradores;
    private long totalColaboradores;
}
