package com.pontuaplus.pontua_plus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import com.pontuaplus.pontua_plus.enums.TipoUsuario;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroColaboradorDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Email(message = "E-mail inválido")
    @NotBlank(message = "E-mail é obrigatório")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotBlank(message = "Matrícula é obrigatória")
    private String matricula;

    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @NotBlank(message = "Colégio é obrigatório")
    private String colegio;

    private LocalDate dataNascimento;

    @NotNull(message = "Tipo de usuário é obrigatório")
    private TipoUsuario tipo;
}
