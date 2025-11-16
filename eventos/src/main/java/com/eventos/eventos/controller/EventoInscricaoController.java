package com.eventos.eventos.controller;

import com.eventos.eventos.model.Evento;
import com.eventos.eventos.model.Inscricao;
import com.eventos.eventos.model.Usuario;
import com.eventos.eventos.repository.EventoRepository;
import com.eventos.eventos.repository.InscricaoRepository;
import com.eventos.eventos.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/eventos")
public class EventoInscricaoController {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    

    @PostMapping("/{id}/inscrever")
    public ResponseEntity<?> inscreverUsuario(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String login = userDetails.getUsername();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNomeUsuarioOrEmail(login, login);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("erro", "USUARIO_NAO_ENCONTRADO_NO_TOKEN"));
        }

        Usuario usuario = usuarioOpt.get();
        Long usuarioId = usuario.getId();

        Evento evento = eventoRepository.findById(id)
                .orElse(null);

        if (evento == null) {
            return ResponseEntity.badRequest().body(Map.of("erro", "EVENTO_NAO_ENCONTRADO"));
        }

        boolean jaInscrito = inscricaoRepository.findByEventoIdAndUsuarioId(id, usuarioId).isPresent();
        if (jaInscrito) {
            return ResponseEntity.badRequest().body(Map.of("erro", "JA_INSCRITO"));
        }

        if (evento.getVagas() != null && evento.getVagas() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("erro", "VAGAS_ESGOTADAS"));
        }

        Inscricao nova = new Inscricao(usuario, evento);
        inscricaoRepository.save(nova);

        if (evento.getVagas() != null) {
            evento.setVagas(evento.getVagas() - 1);
            eventoRepository.save(evento);
        }

        return ResponseEntity.ok(Map.of(
                "mensagem", "Inscrição realizada com sucesso!",
                "eventoId", id));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> verificarStatusInscricao(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String login = userDetails.getUsername();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNomeUsuarioOrEmail(login, login);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("erro", "USUARIO_NAO_ENCONTRADO_NO_TOKEN"));
        }

        Long usuarioId = usuarioOpt.get().getId();

        Map<String, Object> status = new HashMap<>();

        boolean jaInscrito = inscricaoRepository.findByEventoIdAndUsuarioId(id, usuarioId).isPresent();
        Evento evento = eventoRepository.findById(id).orElse(null);

        status.put("jaInscrito", jaInscrito);

        boolean esgotado = evento != null && evento.getVagas() != null &&
                evento.getVagas() <= 0;
        status.put("esgotado", esgotado);
        status.put("prazoExpirado", false);

        return ResponseEntity.ok(status);
    }
}