package com.pontuaplus.pontua_plus.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // Pega o tipo ROLE_XXXX do usuário logado
        String role = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        switch (role) {
            case "ROLE_ALUNO":
                response.sendRedirect("/dashboard.html");
                break;

            case "ROLE_RESPONSAVEL":
                response.sendRedirect("/responsavel/dashboard.html");
                break;

            case "ROLE_PROFESSOR":
                response.sendRedirect("/professor-dashboard.html");
                break;

            case "ROLE_ADMINISTRADOR":
                response.sendRedirect("/adm/dashboard.html");
                break;

            case "ROLE_COORDENADOR":
                response.sendRedirect("/adm/dashboard.html");
                break;

            case "ROLE_DIRETOR":
                response.sendRedirect("/diretor/dashboard.html");
                break;

            case "ROLE_DEV":
                response.sendRedirect("/dev/dashboard.html");
                break;

            default:
                response.sendRedirect("/home");
                break;
        }
    }
}
