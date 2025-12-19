package com.pontuaplus.pontua_plus.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // mapeia /login -> /login.html (arquivo em src/main/resources/static)
        registry.addViewController("/login").setViewName("forward:/login.html");
        registry.addViewController("/registro").setViewName("forward:/registro.html");
        registry.addViewController("/perfil").setViewName("forward:/perfil.html");
        registry.addViewController("/dashboard").setViewName("forward:/dashboard.html");
        registry.addViewController("/").setViewName("forward:/index.html");

    }
}
