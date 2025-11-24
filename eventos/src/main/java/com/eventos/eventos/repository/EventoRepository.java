package com.eventos.eventos.repository;

import com.eventos.eventos.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    // Eventos criados pelo usuário
    List<Evento> findByCriadorId(Long criadorId);

    // Busca por nome ou descrição
    List<Evento> findByNomeContainingIgnoreCaseOrDescricaoContainingIgnoreCase(
            String nomeQuery,
            String descricaoQuery);

    // Busca por categoria
    List<Evento> findByCategoriaIgnoreCase(String categoria);

    // 🔥 Buscar apenas eventos com data futura ou hoje
    @Query("SELECT e FROM Evento e WHERE e.data >= :hoje ORDER BY e.data ASC")
    List<Evento> findEventosFuturos(@Param("hoje") LocalDate hoje);
}
