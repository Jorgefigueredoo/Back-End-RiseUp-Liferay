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

    // Lista de endpoints públicos
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/auth/",
            "/api/test",
            "/health",
            "/",
            "/api/eventos",       
            "/api/perfis/buscar", 
            "/fotos/"             
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        final String requestTokenHeader = request.getHeader("Authorization");

        // Log para depuração (apenas em dev)
        // logger.info("Processando requisição para: " + requestPath);

        // 1. Se for endpoint público e NÃO tiver token, deixa passar direto
        if (isPublicEndpoint(requestPath) && requestTokenHeader == null) {
            chain.doFilter(request, response);
            return;
        }

        String username = null;
        String jwtToken = null;

        // 2. Tenta extrair o token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.error("Erro ao pegar JWT Token: " + e.getMessage());
            } catch (ExpiredJwtException e) {
                logger.warn("JWT Token expirado");
            } catch (Exception e) {
                logger.error("Erro desconhecido no token: " + e.getMessage());
            }
        } else {
            if (!isPublicEndpoint(requestPath)) {
                logger.warn("JWT Token não começa com Bearer ou está ausente. Header: " + requestTokenHeader);
            }
        }

        // 3. Validação
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

    private boolean isPublicEndpoint(String requestPath) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(requestPath::startsWith);
    }
}