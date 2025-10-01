package com.vitalcajavet.msagendamentoconsultas.service;

import com.vitalcajavet.msagendamentoconsultas.model.Veterinario;
import com.vitalcajavet.msagendamentoconsultas.repository.VeterinarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;

    public VeterinarioService(VeterinarioRepository veterinarioRepository) {
        this.veterinarioRepository = veterinarioRepository;
    }

    public List<Veterinario> findAll() {
        return veterinarioRepository.findAll();
    }

    public List<Veterinario> findAllAtivos() {
        return veterinarioRepository.findAllAtivos();
    }

    public Optional<Veterinario> findById(Long id) {
        return veterinarioRepository.findById(id);
    }

    public Optional<Veterinario> findByCpf(String cpf) {
        return veterinarioRepository.findByCpf(cpf);
    }

    public boolean existsById(Long id) {
        return veterinarioRepository.existsById(id);
    }

    public Veterinario save(Veterinario veterinario) {
        veterinarioRepository.findByCpf(veterinario.getCpf())
                .ifPresent(v -> { throw new RuntimeException("CPF já cadastrado para outro veterinário."); });

        return veterinarioRepository.save(veterinario);
    }

    public void deleteById(Long id) {
        veterinarioRepository.deleteById(id);
    }

    public Veterinario update(Long id, Veterinario veterinarioDetails) {
        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veterinário não encontrado com ID: " + id));

        veterinario.setNome(veterinarioDetails.getNome());
        veterinario.setSexo(veterinarioDetails.getSexo());
        veterinario.setCpf(veterinarioDetails.getCpf()); 
        veterinario.setEspecialidade(veterinarioDetails.getEspecialidade());
        veterinario.setAtivo(veterinarioDetails.getAtivo());

        return veterinarioRepository.save(veterinario);
    }

    public Veterinario desativar(Long id) {
        Veterinario veterinario = veterinarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veterinário não encontrado com ID: " + id));

        veterinario.setAtivo(false);
        return veterinarioRepository.save(veterinario);
    }

    public List<Veterinario> findByNome(String nome) {
        return veterinarioRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Veterinario> findByEspecialidade(String especialidade) {
        return veterinarioRepository.findByEspecialidadeContainingIgnoreCase(especialidade);
    }

    public long countAtivos() {
        return veterinarioRepository.countAtivos();
    }
}
