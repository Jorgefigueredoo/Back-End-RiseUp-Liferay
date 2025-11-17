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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/inscricoes")
public class InscricaoPerfilController {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario buscarUsuarioLogado(UserDetails userDetails) {
        if (userDetails == null) return null;
        return usuarioRepository
                .findByNomeUsuarioOrEmail(
                        userDetails.getUsername(),
                        userDetails.getUsername()
                ).orElse(null);
    }

    // =====================================================
    // 1) INSCRIÇÃO
    // =====================================================
    @PostMapping("/eventos/{id}/inscrever")
    public ResponseEntity<?> inscreverUsuario(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = buscarUsuarioLogado(userDetails);
        if (usuario == null)
            return ResponseEntity.status(401).body(Map.of("erro", "USUARIO_NAO_ENCONTRADO"));

        Evento evento = eventoRepository.findById(id).orElse(null);
        if (evento == null)
            return ResponseEntity.badRequest().body(Map.of("erro", "EVENTO_NAO_ENCONTRADO"));

        boolean jaInscrito = inscricaoRepository.findByEventoIdAndUsuarioId(id, usuario.getId()).isPresent();
        if (jaInscrito)
            return ResponseEntity.badRequest().body(Map.of("erro", "JA_INSCRITO"));

        if (evento.getVagas() != null && evento.getVagas() <= 0)
            return ResponseEntity.badRequest().body(Map.of("erro", "SEM_VAGAS"));

        Inscricao nova = new Inscricao(usuario, evento);
        inscricaoRepository.save(nova);

        if (evento.getVagas() != null) {
            evento.setVagas(evento.getVagas() - 1);
            eventoRepository.save(evento);
        }

        return ResponseEntity.ok(Map.of(
                "mensagem", "Inscrição realizada",
                "eventoId", id
        ));
    }

    // =====================================================
    // 2) CANCELAR
    // =====================================================
    @DeleteMapping("/eventos/{id}/cancelar")
    public ResponseEntity<?> cancelar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = buscarUsuarioLogado(userDetails);
        if (usuario == null)
            return ResponseEntity.status(401).body(Map.of("erro", "USUARIO_NAO_ENCONTRADO"));

        Inscricao inscricao = inscricaoRepository.findByEventoIdAndUsuarioId(id, usuario.getId())
                .orElse(null);

        if (inscricao == null)
            return ResponseEntity.badRequest().body(Map.of("erro", "NAO_ESTA_INSCRITO"));

        inscricaoRepository.delete(inscricao);

        Evento evento = inscricao.getEvento();
        if (evento.getVagas() != null) {
            evento.setVagas(evento.getVagas() + 1);
            eventoRepository.save(evento);
        }

        return ResponseEntity.ok(Map.of("mensagem", "Inscrição cancelada"));
    }

    // =====================================================
    // 3) STATUS PARA O DETALHES DO EVENTO
    // =====================================================
    @GetMapping("/eventos/{id}/status")
    public ResponseEntity<?> verificarStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = buscarUsuarioLogado(userDetails);
        if (usuario == null)
            return ResponseEntity.status(401).body(Map.of("erro", "USUARIO_NAO_ENCONTRADO"));

        boolean inscrito = inscricaoRepository.findByEventoIdAndUsuarioId(id, usuario.getId()).isPresent();

        return ResponseEntity.ok(Map.of(
                "eventoId", id,
                "inscrito", inscrito
        ));
    }

    // =====================================================
    // 4) MINHAS INSCRIÇÕES (correto para o JS)
    // =====================================================
    @GetMapping("/minhas-inscricoes")
    public ResponseEntity<?> listarMinhas(
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = buscarUsuarioLogado(userDetails);
        if (usuario == null)
            return ResponseEntity.status(401).body(Map.of("erro", "USUARIO_NAO_AUTENTICADO"));

        List<Inscricao> inscricoes =
                inscricaoRepository.findByUsuarioIdOrderByEventoDataAsc(usuario.getId());

        return ResponseEntity.ok(inscricoes);
    }

    // =====================================================
    // 5) HISTÓRICO (filtra eventos anteriores)
    // =====================================================
    @GetMapping("/historico")
    public ResponseEntity<?> listarHistorico(
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = buscarUsuarioLogado(userDetails);
        if (usuario == null)
            return ResponseEntity.status(401).body(Map.of("erro", "USUARIO_NAO_AUTENTICADO"));

        List<Inscricao> historico =
                inscricaoRepository.findByUsuarioIdOrderByEventoDataAsc(usuario.getId())
                        .stream()
                        .filter(i -> i.getEvento().getData().isBefore(LocalDate.now()))
                        .toList();

        return ResponseEntity.ok(historico);
    }
}
