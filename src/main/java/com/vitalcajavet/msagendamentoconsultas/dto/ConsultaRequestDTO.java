package com.vitalcajavet.msagendamentoconsultas.dto;

import com.vitalcajavet.msagendamentoconsultas.model.enums.TipoConsulta;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaRequestDTO {

    @NotNull(message = "ID do animal é obrigatório")
    private Long animalId;

    @NotNull(message = "ID do veterinário é obrigatório")
    private Long veterinarioId;

    @NotNull(message = "Data e hora são obrigatórias")
    @Future(message = "Data e hora devem ser futuras")
    private LocalDateTime dataHora;

    @NotNull(message = "Tipo de consulta é obrigatório")
    private TipoConsulta tipo;
}