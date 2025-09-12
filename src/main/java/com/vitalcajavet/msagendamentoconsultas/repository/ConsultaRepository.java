package com.vitalcajavet.msagendamentoconsultas.repository;

import com.vitalcajavet.msagendamentoconsultas.model.Consulta;
import com.vitalcajavet.msagendamentoconsultas.model.enums.StatusConsulta;
import com.vitalcajavet.msagendamentoconsultas.model.enums.TipoConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findAll();

    Optional<Consulta> findById(Long id);

    List<Consulta> findByVeterinarioId(Long veterinarioId);

    List<Consulta> findByAnimalId(Long animalId);

    List<Consulta> findByStatus(StatusConsulta status);

    List<Consulta> findByTipo(TipoConsulta tipo);

    @Query("SELECT COUNT(c) > 0 FROM Consulta c WHERE " +
            "c.veterinarioId = :veterinarioId AND " +
            "c.dataHora = :dataHora AND " +
            "c.status <> 'CANCELADA'")
    boolean existsByVeterinarioIdAndDataHora(
            @Param("veterinarioId") Long veterinarioId,
            @Param("dataHora") LocalDateTime dataHora
    );

    @Query("SELECT c FROM Consulta c WHERE " +
            "c.veterinarioId = :veterinarioId AND " +
            "CAST(c.dataHora AS date) = CAST(:data AS date) AND " +
            "c.status <> 'CANCELADA'")
    List<Consulta> findByVeterinarioIdAndData(
            @Param("veterinarioId") Long veterinarioId,
            @Param("data") LocalDateTime data
    );

    @Query("SELECT c.dataHora FROM Consulta c WHERE " +
            "c.veterinarioId = :veterinarioId AND " +
            "CAST(c.dataHora AS date) = CAST(:data AS date) AND " +
            "c.status <> 'CANCELADA'")
    List<LocalDateTime> findHorariosOcupadosByVeterinarioIdAndData(
            @Param("veterinarioId") Long veterinarioId,
            @Param("data") LocalDateTime data
    );

    @Query("SELECT CASE WHEN (:agora < c.dataHora - 2 HOUR) THEN true ELSE false END " +
            "FROM Consulta c WHERE c.id = :id")
    boolean podeSerCancelada(
            @Param("id") Long id,
            @Param("agora") LocalDateTime agora
    );

    @Query("SELECT c FROM Consulta c WHERE c.dataHora >= :agora AND c.status = 'AGENDADA'")
    List<Consulta> findConsultasFuturas(@Param("agora") LocalDateTime agora);

    @Query("SELECT c FROM Consulta c WHERE " +
            "c.veterinarioId = :veterinarioId AND " +
            "c.dataHora BETWEEN :inicio AND :fim AND " +
            "c.status <> 'CANCELADA'")
    List<Consulta> findByVeterinarioIdAndPeriodo(
            @Param("veterinarioId") Long veterinarioId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    @Query("SELECT c FROM Consulta c WHERE CAST(c.dataHora AS date) = CURRENT_DATE")
    List<Consulta> findConsultasDeHoje();
}