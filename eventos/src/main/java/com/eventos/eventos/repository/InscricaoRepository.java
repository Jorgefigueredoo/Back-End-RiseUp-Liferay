package com.eventos.eventos.repository;

import com.eventos.eventos.model.Inscricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    Optional<Inscricao> findByEventoIdAndUsuarioId(Long eventoId, Long usuarioId);

    int countByEventoId(Long eventoId);

    List<Inscricao> findByUsuarioIdOrderByEventoDataAsc(Long usuarioId);

    // 1. QUERY ATIVAS/FUTURAS (MySQL Nativo)
    // Combina 'data' e 'hora' em um string TIMESTAMP para comparação.
    @Query(value = "SELECT * FROM inscricao i JOIN evento e ON i.evento_id = e.id WHERE i.usuario_id = :usuarioId AND i.status != 'CANCELADA' AND CONCAT(e.data, ' ', e.hora) >= :agora", nativeQuery = true)
    List<Inscricao> findActiveInscricoesByUsuarioId(@Param("usuarioId") Long usuarioId, @Param("agora") LocalDateTime agora);

    // 2. QUERY HISTÓRICO/CONCLUÍDOS (MySQL Nativo)
    // Combina 'data' e 'hora' em um string TIMESTAMP para comparação.
    @Query(value = "SELECT * FROM inscricao i JOIN evento e ON i.evento_id = e.id WHERE i.usuario_id = :usuarioId AND i.status = 'CONCLUIDO' AND CONCAT(e.data, ' ', e.hora) < :agora", nativeQuery = true)
    List<Inscricao> findHistoricoInscricoesByUsuarioId(@Param("usuarioId") Long usuarioId, @Param("agora") LocalDateTime agora);
}