package com.baratieri.automaster.repositories;

import com.baratieri.automaster.entities.Peca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PecaRepository extends JpaRepository<Peca, Long> {

    // Busca rápida pelo código de barras/SKU
    Optional<Peca> findBySku(String sku);

    // Busca inteligente: Nome ou PartNumber (Para a barra de pesquisa geral)
    // Query: SELECT * FROM peca WHERE nome LIKE %x% OR part_number LIKE %x%
    List<Peca> findByNomeContainingIgnoreCaseOrPartNumberContainingIgnoreCase(String nome, String partNumber);
}