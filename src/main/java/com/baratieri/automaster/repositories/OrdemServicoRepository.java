package com.baratieri.automaster.repositories;

import com.baratieri.automaster.entities.OrdemServico;
import com.baratieri.automaster.entities.enums.StatusOS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    // Kanban da Oficina: Buscar todas as O.S. que estão "EM_EXECUCAO"
    List<OrdemServico> findByStatus(StatusOS status);

    // Histórico do Cliente: Ver tudo que aquele carro já fez
    List<OrdemServico> findByVeiculoPlaca(String placa);
}