package com.vitalcajavet.msagendamentoconsultas.repository;

import com.vitalcajavet.msagendamentoconsultas.model.Consulta;
import com.vitalcajavet.msagendamentoconsultas.model.enums.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    Optional<Consulta> findById(Long id);

    List<Consulta> findAll();

    @Query("SELECT COUNT(c) > 0 FROM Consulta c WHERE " +
            "c.veterinarioId = :veterinarioId AND " +
            "c.dataHora = :dataHora AND " +
            "c.status <> 'CANCELADA'")
    boolean existsByVeterinarioIdAndDataHoraAndStatusNotCancelada(
            @Param("veterinarioId") Long veterinarioId,
            @Param("dataHora") LocalDateTime dataHora
    );

    @Query("SELECT c FROM Consulta c WHERE " +
            "c.veterinarioId = :veterinarioId AND " +
            "c.dataHora BETWEEN :startDateTime AND :endDateTime AND " +
            "c.status <> 'CANCELADA'")
    List<Consulta> findByVeterinarioIdAndDataHoraBetween(
            @Param("veterinarioId") Long veterinarioId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    @Query("SELECT c FROM Consulta c WHERE " +
            "c.veterinarioId = :veterinarioId AND " +
            "CAST(c.dataHora AS date) = CAST(:data AS date) AND " +
            "c.status <> 'CANCELADA'")
    List<Consulta> findByVeterinarioIdAndData(
            @Param("veterinarioId") Long veterinarioId,
            @Param("data") LocalDateTime data
    );

    @Query("SELECT c FROM Consulta c WHERE " +
            "c.veterinarioId = :veterinarioId AND " +
            "c.status <> 'CANCELADA'")
    List<Consulta> findAgendadasByVeterinarioId(@Param("veterinarioId") Long veterinarioId);

    @Query("SELECT c FROM Consulta c WHERE " +
            "c.id = :id AND " +
            "c.status = :status")
    Optional<Consulta> findByIdAndStatus(
            @Param("id") Long id,
            @Param("status") StatusConsulta status
    );

    @Query("SELECT CASE WHEN (:agora + 2 HOUR) <= c.dataHora THEN true ELSE false END FROM Consulta c WHERE c.id = :id")
    boolean podeCancelar(
            @Param("id") Long id,
            @Param("agora") LocalDateTime agora
    );

    @Query("SELECT c FROM Consulta c WHERE " +
            "c.veterinarioId = :veterinarioId AND " +
            "c.dataHora > :agora AND " +
            "c.status = 'AGENDADA'")
    List<Consulta> findFuturasByVeterinarioId(
            @Param("veterinarioId") Long veterinarioId,
            @Param("agora") LocalDateTime agora
    );

    @Query("SELECT c.status, COUNT(c) FROM Consulta c GROUP BY c.status")
    List<Object[]> countByStatus();
}