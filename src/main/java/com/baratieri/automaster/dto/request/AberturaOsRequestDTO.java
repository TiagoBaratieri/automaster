package com.baratieri.automaster.dto.request;

import com.baratieri.automaster.validation.Placa;
import jakarta.validation.constraints.Size;


public record AberturaOsRequestDTO(
        @Placa
        String placaVeiculo,

        @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres")
        String observacaoInicial
) {}