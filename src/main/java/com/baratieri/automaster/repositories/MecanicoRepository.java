package com.baratieri.automaster.repositories;

import com.baratieri.automaster.entities.Mecanico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MecanicoRepository extends JpaRepository<Mecanico, Long> {

    // Para listar apenas quem está trabalhando na empresa hoje
    // O Spring traduz isso para: SELECT * FROM mecanico WHERE ativo = true
    List<Mecanico> findByAtivoTrue();
}