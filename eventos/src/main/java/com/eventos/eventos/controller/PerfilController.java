package com.eventos.eventos.controller;

import com.eventos.eventos.model.Perfil;
import com.eventos.eventos.model.Usuario;
import com.eventos.eventos.model.Evento;
import com.eventos.eventos.dto.PerfilUpdateDto;
import com.eventos.eventos.dto.ResultadoBuscaDTO;
import com.eventos.eventos.repository.PerfilRepository;
import com.eventos.eventos.repository.UsuarioRepository;
import com.eventos.eventos.repository.EventoRepository;
import com.eventos.eventos.service.FileStorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/perfis")
@CrossOrigin(origins = "*") // Permite acesso de qualquer origem (Vercel, Localhost)
public class PerfilController {

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private EventoRepository eventoRepository;

    // --- ENDPOINTS DE PERFIL ---

    // 1. BUSCAR MEU PERFIL (Com Auto-Criação)
    // Evita o erro 404 se o usuário acabou de se cadastrar
    @GetMapping("/me")
    public ResponseEntity<?> getMeuPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = buscarUsuarioLogado(userDetails);
        if (usuario == null) return ResponseEntity.status(401).body(Map.of("erro", "Usuário não autenticado"));

        Optional<Perfil> perfilOpt = perfilRepository.findByUsuarioId(usuario.getId());
        
        // 🚀 CORREÇÃO: Se não existir perfil, cria um novo na hora!
        if (perfilOpt.isEmpty()) {
            Perfil novoPerfil = new Perfil();
            novoPerfil.setUsuario(usuario);
            novoPerfil.setNomeCompleto(usuario.getNomeUsuario()); // Usa o login como nome inicial
            novoPerfil.setTitulo("Membro da Comunidade");
            novoPerfil.setSobreMim("Olá! Sou novo por aqui.");
            novoPerfil.setHabilidades(new ArrayList<>()); // Lista vazia para não quebrar o front
            
            // Salva e retorna
            return ResponseEntity.ok(perfilRepository.save(novoPerfil));
        }

        return ResponseEntity.ok(perfilOpt.get());
    }

    // 2. ATUALIZAR MEU PERFIL (Com Segurança)
    @PutMapping("/me")
    public ResponseEntity<?> updateMeuPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PerfilUpdateDto perfilUpdateDto) {

        Usuario usuario = buscarUsuarioLogado(userDetails);
        if (usuario == null) return ResponseEntity.status(401).body(Map.of("erro", "Usuário não autenticado"));

        // Busca ou cria um objeto Perfil (caso não exista por algum motivo)
        Perfil perfil = perfilRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    Perfil p = new Perfil();
                    p.setUsuario(usuario);
                    return p; // Será salvo no final do método
                });

        perfil.setNomeCompleto(perfilUpdateDto.getNomeCompleto());
        perfil.setTitulo(perfilUpdateDto.getTitulo());
        perfil.setSobreMim(perfilUpdateDto.getSobreMim());
        perfil.setHabilidades(perfilUpdateDto.getHabilidades());

        return ResponseEntity.ok(perfilRepository.save(perfil));
    }

    // 3. UPLOAD DE FOTO (Salva no Cloudinary)
    @PostMapping("/foto")
    public ResponseEntity<?> uploadFotoPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {

        Usuario usuario = buscarUsuarioLogado(userDetails);
        if (usuario == null) return ResponseEntity.status(401).body(Map.of("erro", "Usuário não autenticado"));

        // Garante que existe perfil antes de tentar salvar a foto
        Perfil perfil = perfilRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    Perfil p = new Perfil();
                    p.setUsuario(usuario);
                    p.setNomeCompleto(usuario.getNomeUsuario());
                    return perfilRepository.save(p);
                });

        try {
            // O FileStorageService já está configurado para usar o Cloudinary
            String url = fileStorageService.salvarArquivo(file);
            
            perfil.setFotoPerfilUrl(url);
            perfilRepository.save(perfil);

            return ResponseEntity.ok(Map.of("novaUrl", url));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro ao salvar foto: " + e.getMessage()));
        }
    }

    // 4. VISUALIZAR PERFIL DE OUTRO USUÁRIO (Público)
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> getPerfilPublico(@PathVariable Long usuarioId) {
        return perfilRepository.findByUsuarioId(usuarioId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("erro", "Perfil não encontrado")));
    }

    // --- ENDPOINT DE BUSCA GLOBAL ---

    @GetMapping("/buscar")
    public ResponseEntity<List<ResultadoBuscaDTO>> buscarTudo(
            @RequestParam("q") String query,
            @RequestParam(value = "filtro", defaultValue = "todos") String filtro
    ) {
        List<ResultadoBuscaDTO> resultado = new ArrayList<>();

        // Busca em PERFIS
        if (filtro.equals("todos") || filtro.equals("usuarios") || filtro.equals("habilidades")) {
            List<Perfil> perfisEncontrados;

            if (filtro.equals("habilidades")) {
                perfisEncontrados = perfilRepository.findByHabilidadesContaining(query);
            } else if (filtro.equals("usuarios")) {
                perfisEncontrados = perfilRepository.findByNomeOuTitulo(query);
            } else {
                perfisEncontrados = perfilRepository.searchByNomeOuHabilidades(query);
            }

            List<ResultadoBuscaDTO> perfisDTO = perfisEncontrados.stream()
                    .map(p -> new ResultadoBuscaDTO(
                            p.getNomeCompleto(),
                            p.getTitulo() != null ? p.getTitulo() : "Colaborador",
                            "perfil.html?usuarioId=" + p.getUsuario().getId(),
                            p.getFotoPerfilUrl()
                    ))
                    .collect(Collectors.toList());
            
            resultado.addAll(perfisDTO);
        }

        // Busca em EVENTOS
        if (filtro.equals("todos") || filtro.equals("eventos")) {
            List<Evento> eventos = eventoRepository
                    .findByNomeContainingIgnoreCaseOrDescricaoContainingIgnoreCase(query, query);

            List<ResultadoBuscaDTO> eventosDTO = eventos.stream()
                    .map(e -> new ResultadoBuscaDTO(
                            e.getNome(),
                            "Evento",
                            "detalhes-evento.html?id=" + e.getId(),
                            null
                    ))
                    .collect(Collectors.toList());
            
            resultado.addAll(eventosDTO);
        }

        return ResponseEntity.ok(resultado);
    }

    // Método auxiliar para extrair o usuário do token JWT
    private Usuario buscarUsuarioLogado(UserDetails userDetails) {
        if (userDetails == null) return null;
        return usuarioRepository
                .findByNomeUsuarioOrEmail(userDetails.getUsername(), userDetails.getUsername())
                .orElse(null);
    }
}