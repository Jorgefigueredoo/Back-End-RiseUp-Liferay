package com.eventos.eventos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.eventos.eventos.model.Evento;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByCriadorId(Long criadorId);

    List<Evento> findByNomeContainingIgnoreCaseOrDescricaoContainingIgnoreCase(String nomeQuery, String descricaoQuery);

    List<Evento> findByCategoriaIgnoreCase(String categoria);
}