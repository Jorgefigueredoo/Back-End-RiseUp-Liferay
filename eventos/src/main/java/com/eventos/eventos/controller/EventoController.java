package com.eventos.eventos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.eventos.eventos.model.Evento;
import com.eventos.eventos.model.Usuario;
import com.eventos.eventos.repository.EventoRepository;
import com.eventos.eventos.repository.UsuarioRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class EventoController {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/criar")
    public ResponseEntity<?> criarEvento(
            @RequestBody Evento evento,
            @AuthenticationPrincipal UserDetails userDetails) {
        Usuario criador = buscarUsuarioLogado(userDetails);
        if (criador == null) {
            return ResponseEntity.status(401).body(Map.of("erro", "Usuário não autenticado"));
        }

        evento.setCriador(criador);
        Evento eventoSalvo = eventoRepository.save(evento);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoSalvo);
    }

    @GetMapping
    public List<Evento> listarEventos() {
        LocalDate hoje = LocalDate.now();
        return eventoRepository.findEventosFuturos(hoje);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> getEventoPorId(@PathVariable Long id) {
        Optional<Evento> evento = eventoRepository.findById(id);
        return evento.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/meus")
    public ResponseEntity<?> listarMeusEventos(
            @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = buscarUsuarioLogado(userDetails);
        if (usuario == null) {
            return ResponseEntity.status(401).body(Map.of("erro", "Usuário não encontrado"));
        }

        List<Evento> meusEventos = eventoRepository.findByCriadorId(usuario.getId());
        return ResponseEntity.ok(meusEventos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarEvento(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuarioLogado = buscarUsuarioLogado(userDetails);
        if (usuarioLogado == null) {
            return ResponseEntity.status(401).body(Map.of("erro", "Usuário não autenticado"));
        }

        Optional<Evento> eventoOpt = eventoRepository.findById(id);
        if (eventoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", "Evento não encontrado"));
        }

        Evento evento = eventoOpt.get();

        if (evento.getCriador() == null || !evento.getCriador().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("erro", "Você não tem permissão para deletar este evento"));
        }

        eventoRepository.delete(evento);
        return ResponseEntity.ok(Map.of("mensagem", "Evento deletado com sucesso"));
    }

    private Usuario buscarUsuarioLogado(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        String login = userDetails.getUsername();
        return usuarioRepository.findByNomeUsuarioOrEmail(login, login).orElse(null);
    }
}
