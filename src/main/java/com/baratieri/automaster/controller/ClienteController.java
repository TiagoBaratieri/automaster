package com.baratieri.automaster.controller;

import com.baratieri.automaster.dto.request.ClienteRequestDTO;
import com.baratieri.automaster.dto.response.ClienteResponseDTO;
import com.baratieri.automaster.services.ClienteServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteServices clienteServices;

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> salvar(@Valid @RequestBody ClienteRequestDTO dto) {
        ClienteResponseDTO cliDTO = clienteServices.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliDTO);
    }
}
