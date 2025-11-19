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
@CrossOrigin(origins = "*")
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

    @GetMapping("/me")
    public ResponseEntity<?> getMeuPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = buscarUsuarioLogado(userDetails);
        if (usuario == null) return ResponseEntity.status(401).body(Map.of("erro", "Usuário não autenticado"));

        Optional<Perfil> perfilOpt = perfilRepository.findByUsuarioId(usuario.getId());
        if (perfilOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("erro", "Perfil não encontrado"));

        return ResponseEntity.ok(perfilOpt.get());
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMeuPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PerfilUpdateDto perfilUpdateDto) {

        Usuario usuario = buscarUsuarioLogado(userDetails);
        if (usuario == null) return ResponseEntity.status(401).body(Map.of("erro", "Usuário não autenticado"));

        Optional<Perfil> perfilOpt = perfilRepository.findByUsuarioId(usuario.getId());
        if (perfilOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("erro", "Perfil não encontrado"));

        Perfil perfil = perfilOpt.get();
        perfil.setNomeCompleto(perfilUpdateDto.getNomeCompleto());
        perfil.setTitulo(perfilUpdateDto.getTitulo());
        perfil.setSobreMim(perfilUpdateDto.getSobreMim());
        perfil.setHabilidades(perfilUpdateDto.getHabilidades());

        return ResponseEntity.ok(perfilRepository.save(perfil));
    }

    @PostMapping("/foto")
    public ResponseEntity<?> uploadFotoPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {

        Usuario usuario = buscarUsuarioLogado(userDetails);
        if (usuario == null) return ResponseEntity.status(401).body(Map.of("erro", "Usuário não autenticado"));

        Optional<Perfil> perfilOpt = perfilRepository.findByUsuarioId(usuario.getId());
        if (perfilOpt.isEmpty()) return ResponseEntity.status(404).body(Map.of("erro", "Perfil não encontrado"));

        try {
            String url = fileStorageService.salvarArquivo(file);
            Perfil perfil = perfilOpt.get();
            perfil.setFotoPerfilUrl(url);
            perfilRepository.save(perfil);

            return ResponseEntity.ok(Map.of("novaUrl", url));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> getPerfilPublico(@PathVariable Long usuarioId) {
        return perfilRepository.findByUsuarioId(usuarioId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("erro", "Perfil não encontrado")));
    }

    // --- ENDPOINT DE BUSCA GLOBAL (CORRIGIDO COM FILTRO E FOTO) ---

    // --- ENDPOINT DE BUSCA COM FILTROS ESPECÍFICOS ---
    @GetMapping("/buscar")
    public ResponseEntity<List<ResultadoBuscaDTO>> buscarTudo(
            @RequestParam("q") String query,
            @RequestParam(value = "filtro", defaultValue = "todos") String filtro
    ) {
        List<ResultadoBuscaDTO> resultado = new ArrayList<>();

        // --- LÓGICA PARA PERFIS (PESSOAS) ---
        if (filtro.equals("todos") || filtro.equals("usuarios") || filtro.equals("habilidades")) {
            
            List<Perfil> perfisEncontrados;

            // AQUI ESTÁ A CORREÇÃO:
            if (filtro.equals("habilidades")) {
                // Se o filtro for Habilidade, usa o método específico
                perfisEncontrados = perfilRepository.findByHabilidadesContaining(query);
            } else if (filtro.equals("usuarios")) {
                // Se o filtro for Nome (usuarios), usa o método específico
                perfisEncontrados = perfilRepository.findByNomeOuTitulo(query);
            } else {
                // Se for Todos, usa o método geral
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

        // --- LÓGICA PARA EVENTOS ---
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

    private Usuario buscarUsuarioLogado(UserDetails userDetails) {
        if (userDetails == null) return null;
        return usuarioRepository
                .findByNomeUsuarioOrEmail(userDetails.getUsername(), userDetails.getUsername())
                .orElse(null);
    }
}