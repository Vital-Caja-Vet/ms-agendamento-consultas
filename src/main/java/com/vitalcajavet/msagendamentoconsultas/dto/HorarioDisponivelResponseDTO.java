package com.vitalcajavet.msagendamentoconsultas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HorarioDisponivelResponseDTO {
    private Long veterinarioId;
    private String nomeVeterinario;
    private List<LocalDateTime> horariosDisponiveis;
    private List<LocalDateTime> horariosOcupados;
}