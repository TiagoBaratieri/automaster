package com.baratieri.automaster.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AdicionarPecaRequestDTO(
         Long idPeca,
         @NotNull(message = "A quantidade em estoque é obrigatória")
         @PositiveOrZero(message = "O estoque não pode ser negativo")
         Integer quantidade
) {}