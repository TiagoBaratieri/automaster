package com.baratieri.automaster.dto;

import com.baratieri.automaster.entities.OrdemServico;
import com.baratieri.automaster.entities.enums.StatusOS;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdemServicoResponseDTO(
        Long id,
        String protocolo,
        LocalDateTime dataAbertura,
        LocalDateTime dataFechamento,
        StatusOS status,
        DadosVeiculoDTO veiculo,
        String nomeCliente,

        List<ItemPecaDTO> pecas,
        List<ItemServicoDTO> servicos,

        BigDecimal valorTotal
) {

    // --- SUB-RECORDS (Classes internas para organizar) ---

    public record DadosVeiculoDTO(String placa, String modelo, String marca) {}

    public record ItemPecaDTO(
            String nomePeca,
            String sku,
            Integer quantidade,
            BigDecimal precoUnitario,
            BigDecimal subtotal
    ) {}

    public record ItemServicoDTO(
            String descricaoServico,
            String observacaoMecanico,
            Integer quantidade,
            BigDecimal valorCobrado,
            BigDecimal subtotal
    ) {}

    public static OrdemServicoResponseDTO fromEntity(OrdemServico os) {
        if (os == null) return null;

        // 1. Converte a lista de Peças
        List<ItemPecaDTO> pecasDTO = os.getItensPeca().stream()
                .map(item -> new ItemPecaDTO(
                        item.getPeca().getNome(),
                        item.getPeca().getSku(),
                        item.getQuantidade(),
                        item.getPrecoUnitario(),
                        item.getSubtotal()
                )).toList();

        // Converte a lista de Serviços
        List<ItemServicoDTO> servicosDTO = os.getItensServico().stream()
                .map(item -> new ItemServicoDTO(
                        item.getServico().getDescricao(),
                        item.getObservacao(),
                        item.getQuantidade(),
                        item.getValorCobrado(),
                        item.getSubtotal()
                )).toList();

        // Monta o objeto principal
        return new OrdemServicoResponseDTO(
                os.getId(),
                "OS-" + os.getId(), // Exemplo de protocolo
                os.getDataAbertura(),
                os.getDataFechamento(),
                os.getStatus(),
                new DadosVeiculoDTO(
                        os.getVeiculo().getPlaca(),
                        os.getVeiculo().getModelo(),
                        os.getVeiculo().getMarca()
                ),
                os.getVeiculo().getCliente().getNome(),
                pecasDTO,
                servicosDTO,
                os.getValorTotal()
        );
    }
}