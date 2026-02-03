package com.baratieri.automaster.dto.response;

import com.baratieri.automaster.entities.Endereco;

public record EnderecoResponseDTO(
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cep,
        String cidade,
        String estado
) {
    // Método auxiliar para converter a Entidade Endereco em DTO
    public static EnderecoResponseDTO fromEntity(Endereco endereco) {
        if (endereco == null) return null;

        return new EnderecoResponseDTO(
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCep(),
                endereco.getCidade(),
                endereco.getEstado()
        );
    }
}
