package com.baratieri.automaster.config;

import com.baratieri.automaster.dto.AberturaOSDTO;
import com.baratieri.automaster.dto.AdicionarServicoDTO;
import com.baratieri.automaster.dto.OrdemServicoResponseDTO;
import com.baratieri.automaster.entities.*;
import com.baratieri.automaster.repositories.ClienteRepository;
import com.baratieri.automaster.repositories.PecaRepository;
import com.baratieri.automaster.repositories.ServicoRepository;
import com.baratieri.automaster.repositories.VeiculoRepository;
import com.baratieri.automaster.services.OsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;


@Configuration
public class TestConfig {

    @Bean
    public CommandLineRunner testarCenarioCompleto(
            OsService osService, // Injetamos o Service (O Maestro)
            ClienteRepository clienteRepo,
            VeiculoRepository veiculoRepo,
            PecaRepository pecaRepo,
            ServicoRepository servicoRepo
    ) {

        return args -> {
            // 1. PREPARAÇÃO DO TERRENO (Cria dados básicos se não existirem)
            // ----------------------------------------------------------------
            if (pecaRepo.count() == 0) {
                criarPecasEServicos(pecaRepo, servicoRepo);
            }

            // Criamos um cliente e carro para o teste
            String placaTeste = "DEV-2026";
            if (veiculoRepo.findByPlaca(placaTeste).isEmpty()) {
                Cliente cli = new Cliente();
                cli.setNome("Thiago Lemos");
                cli.setCpfOuCnpj("12345678900");
                clienteRepo.save(cli);

                Veiculo v = new Veiculo();
                v.setPlaca(placaTeste);
                v.setModelo("Meriva");
                v.setMarca("Chevrolet"); // Adicionei marca para ficar bonito no DTO
                v.setCliente(cli);
                veiculoRepo.save(v);
            }

            // 2. SIMULAÇÃO DO ATENDIMENTO (O Teste Real)
            // ----------------------------------------------------------------
            System.out.println("\n🚀 INICIANDO SIMULAÇÃO DE ORDEM DE SERVIÇO...\n");

            // CENÁRIO A: Recepcionista abre a O.S.
            System.out.println("1. Recepcionista criando a O.S...");

            AberturaOSDTO inputDTO = new AberturaOSDTO(placaTeste, "Barulho ao frear");

            // O Service retorna o DTO de Resposta (Output)
            OrdemServicoResponseDTO osAberta = osService.abrirOS(inputDTO);

            System.out.println("✅ O.S. Aberta com Sucesso! ID: " + osAberta.id());
            System.out.println("   Status: " + osAberta.status());

            // CENÁRIO B: Mecânico adiciona Peça
            System.out.println("\n2. Mecânico adicionando Óleo...");
            Peca oleo = pecaRepo.findAll().get(0); // Pega a primeira peça do banco

            // Chama o service (aqui estou assumindo que você manteve o método retornando a Entidade ou DTO)
            // Se seu service retorna Entidade, convertemos aqui para ver o resultado:
            OrdemServicoResponseDTO osComPeca = osService.adicionarPecaOs(osAberta.id(), oleo.getId(), 4);

            // CENÁRIO C: Mecânico adiciona Serviço
            System.out.println("3. Mecânico lançando Mão de Obra...");
            Servico maoDeObra = servicoRepo.findAll().get(0);

            AdicionarServicoDTO dtoPedidoServico = new AdicionarServicoDTO(
                    maoDeObra.getId(),
                    1,
                    null,
                    "Troca padrão"
            );

            OrdemServicoResponseDTO osFinal = osService.adicionarServico(osAberta.id(), dtoPedidoServico);
            // 3. O GRAN FINALE (Visualizando o DTO de Resposta)
            // ----------------------------------------------------------------
            System.out.println("\n📊 RELATÓRIO FINAL (Como o JSON vai para o Front):");
        };
    }

    // Método auxiliar para popular o básico (se não usar data.sql)
    private void criarPecasEServicos(PecaRepository pRepo, ServicoRepository sRepo) {
        Peca p1 = new Peca();
        p1.setNome("Óleo 5W30");
        p1.setSku("OLE-5W30");
        p1.setPrecoVenda(new BigDecimal("45.00"));
        p1.setQuantidadeEstoque(100);
        p1.setVersion(0L); // Importante por causa do @Version
        pRepo.save(p1);

        Servico s1 = new Servico();
        s1.setDescricao("Troca de Óleo");
        s1.setValorMaoDeObraBase(new BigDecimal("80.00"));
        sRepo.save(s1);
    }
}