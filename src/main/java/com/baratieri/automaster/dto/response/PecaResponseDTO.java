package com.baratieri.automaster.dto.response;

import com.baratieri.automaster.entities.Peca;

import java.math.BigDecimal;

public record PecaResponseDTO(
        Long id,
        String nome,
        String sku,
        BigDecimal precoVenda,
        Integer quantidadeEstoque
) {
    public static PecaResponseDTO fromEntity(Peca peca) {
        if (peca == null) return null;
        return new PecaResponseDTO(
                peca.getId(),
                peca.getNome(),
                peca.getSku(),
                peca.getPrecoVenda(),
                peca.getQuantidadeEstoque()
        );
    }
}