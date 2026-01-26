package com.baratieri.automaster.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;


import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mecanico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    // CPF é crucial para questões fiscais/trabalhistas
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    // Especialidade (Ex: "Eletricista", "Suspensão", "Geral")
    private String especialidade;

    // COMISSÃO (%)
    // Muitas oficinas pagam % sobre a Mão de Obra.
    // Ex: 0.10 para 10%
    @Column(precision = 5, scale = 2)
    private BigDecimal taxaComissao;

    // SOFT DELETE
    // Se o mecânico for demitido, marcamos false.
    // Ele não aparece na lista de novos serviços, mas o histórico permanece.
    @Column(nullable = false)
    private Boolean ativo = true;
}