package com.baratieri.automaster.entities;

import com.baratieri.automaster.entities.enums.StatusOS;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Veiculo veiculo;

    @Enumerated(EnumType.STRING)
    private StatusOS status;

    @Column(nullable = false)
    private LocalDateTime dataAbertura;

    private LocalDateTime dataFechamento;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ItemPeca> itensPeca = new ArrayList<>(); // SOLUÇÃO 2: Inicialize a lista!

    // Faça o mesmo para serviços
    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ItemServico> itensServico = new ArrayList<>();

    public void calcularTotal() {
        // 1. Soma o total das PEÇAS
        // Se a lista for nula (o que não deve acontecer se inicializada), considera zero.
        BigDecimal totalPecas = BigDecimal.ZERO;

        if (itensPeca != null && !itensPeca.isEmpty()) {
            totalPecas = itensPeca.stream()
                    .map(item -> item.getSubtotal()) // Chama o getSubtotal() de cada item
                    .filter(valor -> valor != null)  // Segurança contra nulos
                    .reduce(BigDecimal.ZERO, BigDecimal::add); // Soma acumulada
        }

        // 2. Soma o total dos SERVIÇOS
        BigDecimal totalServicos = BigDecimal.ZERO;

        if (itensServico != null && !itensServico.isEmpty()) {
            totalServicos = itensServico.stream()
                    .map(item -> item.getSubtotal())
                    .filter(valor -> valor != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // 3. Atualiza o atributo da própria classe
        this.valorTotal = totalPecas.add(totalServicos);
    }

    @PrePersist
    @PreUpdate
    public void garantirCalculoAntesDeSalvar() {
        this.calcularTotal();
    }
}
