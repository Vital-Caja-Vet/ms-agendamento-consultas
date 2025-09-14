package com.vitalcajavet.msagendamentoconsultas.controller;

import com.vitalcajavet.msagendamentoconsultas.dto.ConsultaRequestDTO;
import com.vitalcajavet.msagendamentoconsultas.dto.ConsultaResponseDTO;
import com.vitalcajavet.msagendamentoconsultas.dto.HorarioDisponivelRequestDTO;
import com.vitalcajavet.msagendamentoconsultas.dto.HorarioDisponivelResponseDTO;
import com.vitalcajavet.msagendamentoconsultas.model.Consulta;
import com.vitalcajavet.msagendamentoconsultas.model.enums.StatusConsulta;
import com.vitalcajavet.msagendamentoconsultas.model.enums.TipoConsulta;
import com.vitalcajavet.msagendamentoconsultas.service.ConsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/consultas")
@CrossOrigin(origins = "*")
@Tag(name = "Consultas", description = "Agendamento e gestão de consultas")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @GetMapping
    @Operation(summary = "Listar todas as consultas")
    public ResponseEntity<List<ConsultaResponseDTO>> listarTodas() {
        List<Consulta> consultas = consultaService.listarTodasConsultas();
        List<ConsultaResponseDTO> responseDTOs = consultas.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar consulta por ID")
    public ResponseEntity<ConsultaResponseDTO> buscarPorId(@PathVariable Long id) {
        return consultaService.buscarPorId(id)
                .map(consulta -> ResponseEntity.ok(convertToResponseDTO(consulta)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Agendar nova consulta")
    public ResponseEntity<ConsultaResponseDTO> agendar(@Valid @RequestBody ConsultaRequestDTO requestDTO) {
        try {
            Consulta consulta = consultaService.agendarConsulta(requestDTO);
            ConsultaResponseDTO responseDTO = convertToResponseDTO(consulta);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (RuntimeException e) {
            throw e; // Será tratado pelo GlobalExceptionHandler
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar consulta")
    public ResponseEntity<ConsultaResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ConsultaRequestDTO requestDTO) {
        try {
            Consulta consulta = consultaService.atualizarConsulta(id, requestDTO);
            ConsultaResponseDTO responseDTO = convertToResponseDTO(consulta);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @DeleteMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar consulta")
    public ResponseEntity<ConsultaResponseDTO> cancelar(@PathVariable Long id) {
        try {
            Consulta consulta = consultaService.cancelarConsulta(id);
            ConsultaResponseDTO responseDTO = convertToResponseDTO(consulta);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @PostMapping("/horarios-disponiveis")
    @Operation(summary = "Listar horários disponíveis")
    public ResponseEntity<HorarioDisponivelResponseDTO> listarHorariosDisponiveis(
            @Valid @RequestBody HorarioDisponivelRequestDTO requestDTO) {
        try {
            HorarioDisponivelResponseDTO response = consultaService.listarHorariosDisponiveis(requestDTO);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @GetMapping("/veterinario/{veterinarioId}")
    @Operation(summary = "Listar consultas por veterinário")
    public ResponseEntity<List<ConsultaResponseDTO>> listarPorVeterinario(@PathVariable Long veterinarioId) {
        List<Consulta> consultas = consultaService.listarConsultasPorVeterinario(veterinarioId);
        List<ConsultaResponseDTO> responseDTOs = consultas.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/animal/{animalId}")
    @Operation(summary = "Listar consultas por animal")
    public ResponseEntity<List<ConsultaResponseDTO>> listarPorAnimal(@PathVariable Long animalId) {
        List<Consulta> consultas = consultaService.listarConsultasPorAnimal(animalId);
        List<ConsultaResponseDTO> responseDTOs = consultas.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar consultas por status")
    public ResponseEntity<List<ConsultaResponseDTO>> listarPorStatus(@PathVariable StatusConsulta status) {
        List<Consulta> consultas = consultaService.listarConsultasPorStatus(status);
        List<ConsultaResponseDTO> responseDTOs = consultas.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Listar consultas por tipo")
    public ResponseEntity<List<ConsultaResponseDTO>> listarPorTipo(@PathVariable TipoConsulta tipo) {
        List<Consulta> consultas = consultaService.listarConsultasPorTipo(tipo);
        List<ConsultaResponseDTO> responseDTOs = consultas.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/futuras")
    @Operation(summary = "Listar consultas futuras")
    public ResponseEntity<List<ConsultaResponseDTO>> listarFuturas() {
        List<Consulta> consultas = consultaService.listarConsultasFuturas();
        List<ConsultaResponseDTO> responseDTOs = consultas.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/hoje")
    @Operation(summary = "Listar consultas de hoje")
    public ResponseEntity<List<ConsultaResponseDTO>> listarDeHoje() {
        List<Consulta> consultas = consultaService.listarConsultasDeHoje();
        List<ConsultaResponseDTO> responseDTOs = consultas.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status da consulta")
    public ResponseEntity<ConsultaResponseDTO> atualizarStatus(
            @PathVariable Long id, @RequestParam StatusConsulta status) {
        try {
            Consulta consulta = consultaService.atualizarStatus(id, status);
            ConsultaResponseDTO responseDTO = convertToResponseDTO(consulta);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @GetMapping("/disponibilidade")
    @Operation(summary = "Verificar disponibilidade")
    public ResponseEntity<Boolean> verificarDisponibilidade(
            @RequestParam Long veterinarioId, @RequestParam LocalDateTime dataHora) {
        boolean disponivel = consultaService.verificarDisponibilidade(veterinarioId, dataHora);
        return ResponseEntity.ok(disponivel);
    }

    @GetMapping("/periodo")
    @Operation(summary = "Listar consultas por período")
    public ResponseEntity<List<ConsultaResponseDTO>> listarPorPeriodo(
            @RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fim) {
        List<Consulta> consultas = consultaService.listarConsultasPorPeriodo(inicio, fim);
        List<ConsultaResponseDTO> responseDTOs = consultas.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    private ConsultaResponseDTO convertToResponseDTO(Consulta consulta) {
        ConsultaResponseDTO responseDTO = new ConsultaResponseDTO();
        responseDTO.setId(consulta.getId());
        responseDTO.setAnimalId(consulta.getAnimalId());
        responseDTO.setVeterinarioId(consulta.getVeterinarioId());
        responseDTO.setDataHora(consulta.getDataHora());
        responseDTO.setTipo(consulta.getTipo());
        responseDTO.setStatus(consulta.getStatus());
        responseDTO.setCreatedAt(consulta.getCreatedAt());
        responseDTO.setUpdatedAt(consulta.getUpdatedAt());

        responseDTO.setNomeAnimal("Animal #" + consulta.getAnimalId());
        responseDTO.setNomeVeterinario("Veterinário #" + consulta.getVeterinarioId());

        return responseDTO;
    }
}