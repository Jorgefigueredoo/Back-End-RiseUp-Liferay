package com.eventos.eventos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/fotos/**")
                .addResourceLocations("file:uploads/fotos/");
    }

    /**
     * Configuração de CORS: Permite que o domínio do Vercel acesse esta API (Render).
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        
        // 🚀 SEU DOMÍNIO VERCEL DEFINIDO AQUI:
        final String allowedOrigin = "https://rise-up-2025-1-liferay.vercel.app"; 

        registry.addMapping("/**") // Aplica as regras a todos os endpoints
            .allowedOrigins(allowedOrigin) // Permite requisições SOMENTE do seu front-end
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") 
            .allowedHeaders("*")
            .allowCredentials(true); 
    }
}