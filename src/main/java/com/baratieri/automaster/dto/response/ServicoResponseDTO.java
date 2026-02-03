package com.baratieri.automaster.dto.response;

import com.baratieri.automaster.entities.Servico;

import java.math.BigDecimal;

public record ServicoResponseDTO(
        Long id,
        String descricao,
        BigDecimal precoBase
) {
    public static ServicoResponseDTO fromEntity(Servico servico) {
        if (servico == null) return null;
        return new ServicoResponseDTO(
                servico.getId(),
                servico.getDescricao(),
                servico.getValorMaoDeObraBase()
        );
    }
}