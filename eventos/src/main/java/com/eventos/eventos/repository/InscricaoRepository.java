package com.eventos.eventos.repository;

import com.eventos.eventos.model.Inscricao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    Optional<Inscricao> findByEventoIdAndUsuarioId(Long eventoId, Long usuarioId);

    int countByEventoId(Long eventoId);

    List<Inscricao> findByUsuarioIdOrderByEventoDataAsc(Long usuarioId);

}