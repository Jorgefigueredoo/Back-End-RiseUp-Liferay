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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 🚀 CONFIGURAÇÃO DE CORS (Permite tudo para evitar erros de conexão)
        configuration.addAllowedOriginPattern("*"); // Aceita qualquer origem
        
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
            // Aplica a configuração de CORS definida acima
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Desabilita CSRF (padrão para APIs stateless)
            .csrf(csrf -> csrf.disable())
            // Configura as permissões de rotas
            .authorizeHttpRequests(auth -> auth
                // 1. LIBERA O PREFLIGHT (O navegador pergunta "posso conectar?" antes de enviar dados)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // 2. ENDPOINTS PÚBLICOS GERAIS (Sem Login)
                .requestMatchers("/", "/api/test", "/health").permitAll()
                .requestMatchers("/api/auth/**").permitAll() // Login e Registro
                
                // 3. 🚨 REGRA CRUCIAL: O endpoint /me DEVE ser autenticado!
                // Esta linha deve vir ANTES da regra geral de perfis.
                .requestMatchers("/api/perfis/me").authenticated()

                // 4. ENDPOINTS DE LEITURA PÚBLICA (GET)
                .requestMatchers(HttpMethod.GET, "/api/eventos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/perfis/buscar").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/perfis/**").permitAll() // Perfis públicos por ID
                .requestMatchers(HttpMethod.GET, "/fotos/**").permitAll() // Fotos de perfil
                
                // 5. TUDO O MAIS PRECISA DE LOGIN
                .anyRequest().authenticated()
            )
            // Define sessão como Stateless (Não guarda cookies de sessão, usa apenas o Token)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        // Adiciona o filtro JWT antes do filtro padrão de usuário/senha
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}