package com.vitalcajavet.msagendamentoconsultas.dto;

import com.vitalcajavet.msagendamentoconsultas.model.enums.StatusConsulta;
import com.vitalcajavet.msagendamentoconsultas.model.enums.TipoConsulta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaResponseDTO {
    private Long id;
    private Long animalId;
    private Long veterinarioId;
    private LocalDateTime dataHora;
    private TipoConsulta tipo;
    private StatusConsulta status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String nomeAnimal;
    private String nomeVeterinario;
}