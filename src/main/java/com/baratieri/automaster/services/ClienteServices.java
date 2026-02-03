package com.baratieri.automaster.services;

import com.baratieri.automaster.dto.request.ClienteRequestDTO;
import com.baratieri.automaster.dto.response.ClienteResponseDTO;
import com.baratieri.automaster.entities.Cliente;
import com.baratieri.automaster.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ClienteServices {

    private final ClienteRepository clienteRepository;

    @Transactional
    public ClienteResponseDTO salvar(ClienteRequestDTO dto){
        if (clienteRepository.existsByCpfOuCnpj(dto.cpfOuCnpj())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe um cliente cadastrado com este CPF/CNPJ.");
        }

        Cliente cliente = clienteRepository.save(dto.toEntity());
        return ClienteResponseDTO.fromEntity(cliente);
    }
}
