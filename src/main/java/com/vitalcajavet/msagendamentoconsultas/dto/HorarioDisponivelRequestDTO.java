package com.vitalcajavet.msagendamentoconsultas.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HorarioDisponivelRequestDTO {

    @NotNull(message = "ID do veterinário é obrigatório")
    private Long veterinarioId;

    @NotNull(message = "Data é obrigatória")
    private LocalDate data;
}