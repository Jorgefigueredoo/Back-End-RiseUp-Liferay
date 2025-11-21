package com.eventos.eventos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configura o servidor para expor a pasta local "uploads/fotos"
        // através da URL pública "/fotos/**"
        registry.addResourceHandler("/fotos/**")
                .addResourceLocations("file:uploads/fotos/");
    }

    // OBS: A configuração de CORS (addCorsMappings) foi removida intencionalmente.
    // O controle de CORS agora é feito de forma centralizada no SecurityConfig.java
    // para evitar o erro "IllegalArgumentException" no startup do Render.
}