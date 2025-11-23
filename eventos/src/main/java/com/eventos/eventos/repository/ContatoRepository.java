package com.eventos.eventos.repository;

import com.eventos.eventos.model.Contato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContatoRepository extends JpaRepository<Contato, Long> {
    // O JpaRepository já te dá o método save() de graça!
}
