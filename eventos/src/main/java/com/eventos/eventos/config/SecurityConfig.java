package com.eventos.eventos.config;

import com.eventos.eventos.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 🚀 CONFIGURAÇÃO "BALA DE PRATA" PARA CORS
        // Permite qualquer origem (*) - Ideal para resolver problemas de integração
        // rapidamente
        configuration.addAllowedOriginPattern("*");

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Aplica a configuração de CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. LIBERA O PREFLIGHT (ESSENCIAL PARA O NAVEGADOR NÃO BLOQUEAR)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. ENDPOINTS PÚBLICOS (Sem Login)
                        .requestMatchers("/", "/api/test", "/health").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        .requestMatchers("/api/contato/**").permitAll()

                        // 3. 🚨 EXCEÇÃO IMPORTANTE: O endpoint /me PRECISA de autenticação!
                        // Colocamos isso ANTES da regra geral de perfis para não ser confundido.
                        .requestMatchers("/api/perfis/me").authenticated()

                        // 4. ENDPOINTS PÚBLICOS DE LEITURA (GET)
                        .requestMatchers(HttpMethod.GET, "/api/eventos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/perfis/buscar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/perfis/**").permitAll() // Outros perfis são públicos
                        .requestMatchers(HttpMethod.GET, "/fotos/**").permitAll()

                        // 5. TODO O RESTO PRECISA DE LOGIN
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}