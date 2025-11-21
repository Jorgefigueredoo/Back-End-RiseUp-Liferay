package com.eventos.eventos.config;

import com.eventos.eventos.service.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // 🚀 ATUALIZADO: Lista completa de endpoints públicos (para bater com o SecurityConfig)
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/auth/",
            "/api/test",
            "/health",
            "/",
            "/api/eventos",       // Adicionado
            "/api/perfis/buscar", // Adicionado
            "/fotos/"             // Adicionado
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Se for endpoint público E não tiver token, deixa passar sem validar
        // (Mas se tiver token, tentamos validar para identificar o usuário)
        String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader == null && isPublicEndpoint(requestPath)) {
            chain.doFilter(request, response);
            return;
        }

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.error("Não foi possível obter o token JWT");
            } catch (ExpiredJwtException e) {
                logger.error("Token JWT expirou");
            }
        } else {
            // Se não é público e não tem token, logamos o aviso (mas deixamos o SecurityConfig barrar)
            if (!isPublicEndpoint(requestPath)) {
                logger.warn("Token JWT não fornecido para: " + requestPath);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }

    // Verifica se o endpoint é público
    private boolean isPublicEndpoint(String requestPath) {
        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(requestPath::startsWith);
    }
}