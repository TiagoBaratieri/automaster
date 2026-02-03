package com.baratieri.automaster.dto.response;

import com.baratieri.automaster.entities.Veiculo;

public record VeiculoResponseDTO (Long id,
                                  String palca,
                                  String modelo,
                                  String marca,
                                  Integer ano,
                                  Long idProprietario,
                                  String nomeProprietario){

    public static VeiculoResponseDTO fromEntity(Veiculo veiculo){
        if (veiculo == null) return null;

        return new VeiculoResponseDTO(
                veiculo.getId(),
                veiculo.getPlaca(),
                veiculo.getMarca(),
                veiculo.getModelo(),
                veiculo.getAno(),
                veiculo.getCliente().getId(),
                veiculo.getCliente().getNome()
        );
    }
}
