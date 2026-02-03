package com.baratieri.automaster.controller;

import com.baratieri.automaster.dto.request.AberturaOsRequestDTO;
import com.baratieri.automaster.dto.request.AdicionarPecaRequestDTO;
import com.baratieri.automaster.dto.request.AdicionarServicoRequestDTO;
import com.baratieri.automaster.dto.response.OrdemServicoResponseDTO;
import com.baratieri.automaster.services.OsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/os")
@RequiredArgsConstructor
public class OrdemServicoController {

    private final OsService osService;

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(osService.buscarOrdemServicoPorId(id));
    }

    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> abrirOs(@Valid  @RequestBody AberturaOsRequestDTO osdto){
        OrdemServicoResponseDTO osAberta = osService.abrirOS(osdto);
        return ResponseEntity.status(HttpStatus.CREATED).body(osAberta);
    }

    @PostMapping("/{id}/pecas")
    public ResponseEntity<OrdemServicoResponseDTO> adicionarPecaOs(
            @PathVariable Long id,
            @RequestBody AdicionarPecaRequestDTO dto){
        return ResponseEntity.ok(osService.adicionarPecaOs(id, dto));

    }


    @PostMapping("/{id}/servicos")
    public ResponseEntity<OrdemServicoResponseDTO> adicionarServicoOs(
            @PathVariable Long id,
            @RequestBody AdicionarServicoRequestDTO dto){
        return ResponseEntity.ok(osService.adicionarServico(id, dto));
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<OrdemServicoResponseDTO> finalizarOs(@PathVariable Long id){
        return ResponseEntity.ok(osService.finalizarOS(id));
    }

}
