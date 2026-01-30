package com.baratieri.automaster.dto;

import java.math.BigDecimal;

public record AdicionarServicoDTO(
        Long idServico,
        Integer quantidade,
        BigDecimal valorCobrado,
        String observacao
) {}