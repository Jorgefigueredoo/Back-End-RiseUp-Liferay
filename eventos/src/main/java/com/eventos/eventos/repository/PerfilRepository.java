package com.eventos.eventos.repository;

import com.eventos.eventos.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    Optional<Perfil> findByUsuarioId(Long usuarioId);

    // 1. BUSCA GERAL (Todos): Procura em Nome, Título OU Habilidades
    @Query("SELECT DISTINCT p FROM Perfil p " +
           "LEFT JOIN p.habilidades h " +
           "WHERE LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :termo, '%')) " +
           "OR LOWER(p.titulo) LIKE LOWER(CONCAT('%', :termo, '%')) " +
           "OR LOWER(h) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<Perfil> searchByNomeOuHabilidades(@Param("termo") String termo);

    // 2. BUSCA ESPECÍFICA POR HABILIDADE (Filtro: Habilidade)
    // Procura APENAS dentro da lista de habilidades
    @Query("SELECT DISTINCT p FROM Perfil p " +
           "JOIN p.habilidades h " +
           "WHERE LOWER(h) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<Perfil> findByHabilidadesContaining(@Param("termo") String termo);

    // 3. BUSCA ESPECÍFICA POR NOME (Filtro: Nome)
    // Procura APENAS no Nome ou Título
    @Query("SELECT p FROM Perfil p " +
           "WHERE LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :termo, '%')) " +
           "OR LOWER(p.titulo) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<Perfil> findByNomeOuTitulo(@Param("termo") String termo);
}