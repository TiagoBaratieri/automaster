package com.baratieri.automaster.services;

import com.baratieri.automaster.dto.AberturaOSDTO;
import com.baratieri.automaster.dto.AdicionarServicoDTO;
import com.baratieri.automaster.dto.OrdemServicoResponseDTO;
import com.baratieri.automaster.entities.*;
import com.baratieri.automaster.entities.enums.StatusOS;
import com.baratieri.automaster.repositories.OrdemServicoRepository;
import com.baratieri.automaster.repositories.PecaRepository;
import com.baratieri.automaster.repositories.ServicoRepository;
import com.baratieri.automaster.repositories.VeiculoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class OsService {

    private final OrdemServicoRepository osRepository;
    private final VeiculoRepository veiculoRepository;
    private final PecaRepository pecaRepository;
    private final ServicoRepository servicoRepository;

    @Transactional
    public OrdemServicoResponseDTO abrirOS(AberturaOSDTO dto) {
        Veiculo veiculo = veiculoRepository.findByPlaca(dto.placaVeiculo())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não encontrado. Cadastre antes."));

        OrdemServico os = new OrdemServico();
        os.setVeiculo(veiculo);
        os.setStatus(StatusOS.ORCAMENTO);
        os.setDataAbertura(LocalDateTime.now());

        osRepository.save(os);

        return OrdemServicoResponseDTO.fromEntity(os);

    }

    @Transactional
    public OrdemServicoResponseDTO adicionarServico(Long osId, AdicionarServicoDTO dados) {
        OrdemServico os = buscarOsOuFalhar(osId);
        Servico servico = servicoRepository.findById(dados.idServico())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado"));

        validarStatusParaEdicao(os);

        ItemServico item = new ItemServico();
        item.setOrdemServico(os);
        item.setServico(servico);
        item.setQuantidade(dados.quantidade() != null ? dados.quantidade() : 1);
        item.setObservacao(dados.observacao());


        if (dados.valorCobrado() != null) {
            item.setValorCobrado(dados.valorCobrado());
        } else {
            item.setValorCobrado(servico.getValorMaoDeObraBase());
        }

        os.getItensServico().add(item);

        os.calcularTotal();
        osRepository.save(os);

        return OrdemServicoResponseDTO.fromEntity(os);
    }


    public OrdemServicoResponseDTO adicionarPecaOs(Long osId, Long pecaId, Integer quantidade) {
        OrdemServico os = buscarOs(osId);
        Peca peca = pecaRepository.findById(pecaId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Peça não encontrada"));

        validarOSAberta(os); // Não pode mexer em OS fechada

        if (peca.getQuantidadeEstoque() < quantidade) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estoque insuficiente. Disponível: " + peca.getQuantidadeEstoque());
        }

        // 3. Execução (A Mágica)
        ItemPeca item = new ItemPeca();
        item.setOrdemServico(os);
        item.setPeca(peca);
        item.setQuantidade(quantidade);

        // O SNAPSHOT: Gravamos o preço de HOJE. Se a peça aumentar amanhã, essa OS não muda.
        item.setPrecoUnitario(peca.getPrecoVenda());

        // 4. Efeitos Colaterais (Side Effects)
        os.getItensPeca().add(item); // Adiciona na lista

        // BAIXA DE ESTOQUE (Crucial estar dentro do @Transactional)
        peca.setQuantidadeEstoque(peca.getQuantidadeEstoque() - quantidade);
        pecaRepository.save(peca); // Atualiza o estoque da peça

        // 5. Recálculo e Salvamento
        os.calcularTotal(); // Aquele método que criamos na Entidade

        osRepository.save(os);
        return OrdemServicoResponseDTO.fromEntity(os);
    }

    public void finalizarOS(Long id) {
        OrdemServico os = buscarOs(id);
        if(os.getItensPeca().isEmpty() && os.getItensServico().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível finalizar uma OS vazia.");
        }
        os.setStatus(StatusOS.FINALIZADO);
        os.setDataFechamento(LocalDateTime.now());
        osRepository.save(os);
    }

    private OrdemServico buscarOs(Long osId) {
        return osRepository.findById(osId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "OS não encontrada"));
    }

    private void validarOSAberta(OrdemServico os) {
        if (os.getStatus() == StatusOS.FINALIZADO || os.getStatus() == StatusOS.CANCELADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta OS já está finalizada e não pode ser alterada.");
        }
    }

    private void validarStatusParaEdicao(OrdemServico os) {
        if (!os.getStatus().permiteEdicao() ) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O status atual (" + os.getStatus() + ") não permite edições.");
        }
    }

    private OrdemServico buscarOsOuFalhar(Long osId) {
        return osRepository.findById(osId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ordem de Serviço não encontrada (ID: " + osId + ")"
                ));
    }


}
