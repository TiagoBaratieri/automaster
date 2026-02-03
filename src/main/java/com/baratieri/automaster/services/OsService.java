package com.baratieri.automaster.services;

import com.baratieri.automaster.dto.AberturaOSDTO;
import com.baratieri.automaster.dto.AdicionarPecaDTO;
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
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class OsService {

    private final OrdemServicoRepository osRepository;
    private final VeiculoRepository veiculoRepository;
    private final PecaRepository pecaRepository;
    private final ServicoRepository servicoRepository;


    @Transactional
    public OrdemServicoResponseDTO buscarOrdemServicoPorId(Long id){
        Optional<OrdemServico> ordemServico = osRepository.findById(id);
        OrdemServico os = ordemServico.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Ordem de serviço não encontrada"));
        return OrdemServicoResponseDTO.fromEntity(os);
    }


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
        validarStatusParaEdicao(os);

        Servico servico = servicoRepository.findById(dados.idServico())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado"));

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

        return recalcularESalvar(os);
    }


    @Transactional
    public OrdemServicoResponseDTO adicionarPecaOs(Long osId, AdicionarPecaDTO dto) {
        OrdemServico os = buscarOsOuFalhar(osId);
        validarStatusParaEdicao(os);

        Peca peca = pecaRepository.findById(dto.idPeca())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Peça não encontrada"));

        if (peca.getQuantidadeEstoque() < dto.quantidade()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estoque insuficiente.");
        }

        ItemPeca item = new ItemPeca();
        item.setOrdemServico(os);
        item.setPeca(peca);
        item.setQuantidade(dto.quantidade());
        item.setPrecoUnitario(peca.getPrecoVenda());

        os.getItensPeca().add(item);

        peca.setQuantidadeEstoque(peca.getQuantidadeEstoque() - dto.quantidade());
        pecaRepository.save(peca);

        return recalcularESalvar(os);
    }
    public OrdemServicoResponseDTO finalizarOS(Long id) {
        OrdemServico os = buscarOs(id);
        if(os.getItensPeca().isEmpty() && os.getItensServico().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível finalizar uma OS vazia.");
        }
        os.setStatus(StatusOS.FINALIZADO);
        os.setDataFechamento(LocalDateTime.now());
        osRepository.save(os);
        return OrdemServicoResponseDTO.fromEntity(os);
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


    private OrdemServicoResponseDTO recalcularESalvar(OrdemServico os) {
        os.calcularTotal();
        os = osRepository.save(os);
        return OrdemServicoResponseDTO.fromEntity(os);
    }

}
