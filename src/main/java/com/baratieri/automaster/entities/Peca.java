package com.baratieri.automaster.entities;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Peca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // SKU (Stock Keeping Unit): Código interno da loja (ex: "FIL-OLEO-01")
    @Column(nullable = false, unique = true, length = 20)
    private String sku;

    @Column(nullable = false)
    private String nome;

    // Código do Fabricante (ex: Bosch 0986B00015) - Ajuda na busca
    private String partNumber;

    // PREÇO DE VENDA ATUAL
    // Lembre-se: O preço histórico fica na ItemPeca. Este é o preço de prateleira hoje.
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda;

    // PREÇO DE CUSTO
    // Fundamental para relatórios de Lucratividade (ROI)
    @Column(precision = 10, scale = 2)
    private BigDecimal precoCusto;

    // CONTROLE DE ESTOQUE
    @Column(nullable = false)
    private Integer quantidadeEstoque = 0;

    // Nível mínimo para alertar compra (Ponto de Pedido)
    private Integer estoqueMinimo = 5;

    // CONCORRÊNCIA (Nível Doutorado 🎓)
    // Se dois usuários tentarem atualizar essa peça ao mesmo tempo,
    // o Hibernate verifica a versão. Se mudou, lança OptimisticLockException.
    @Version
    private Long version;
}