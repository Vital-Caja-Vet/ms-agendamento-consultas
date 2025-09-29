package com.vitalcajavet.msagendamentoconsultas.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.horario-comercial")
public class HorarioComercialProperties {
    private int inicio;
    private int fim;
    private int horasMinimasCancelamento;
}
