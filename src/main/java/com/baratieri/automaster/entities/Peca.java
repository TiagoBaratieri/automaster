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

    @Column(nullable = false, unique = true, length = 20)
    private String sku;

    @Column(nullable = false)
    private String nome;

    private String partNumber;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda;


    @Column(precision = 10, scale = 2)
    private BigDecimal precoCusto;


    @Column(nullable = false)
    private Integer quantidadeEstoque = 0;

    private Integer estoqueMinimo = 5;

    @Version
    private Long version;
}