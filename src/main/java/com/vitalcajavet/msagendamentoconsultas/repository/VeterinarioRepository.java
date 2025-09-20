package com.vitalcajavet.msagendamentoconsultas.repository;

import com.vitalcajavet.msagendamentoconsultas.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    List<Veterinario> findAll();

    Optional<Veterinario> findById(Long id);

    boolean existsById(Long id);

    Optional<Veterinario> findByCpf(String cpf);

    @Query("SELECT v FROM Veterinario v WHERE v.ativo = true")
    List<Veterinario> findAllAtivos();

    @Query("SELECT v FROM Veterinario v WHERE LOWER(v.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND v.ativo = true")
    List<Veterinario> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    @Query("SELECT COUNT(v) FROM Veterinario v WHERE v.ativo = true")
    long countAtivos();

    @Query("SELECT v FROM Veterinario v WHERE LOWER(v.especialidade) LIKE LOWER(CONCAT('%', :especialidade, '%')) AND v.ativo = true")
    List<Veterinario> findByEspecialidadeContainingIgnoreCase(@Param("especialidade") String especialidade);
}
