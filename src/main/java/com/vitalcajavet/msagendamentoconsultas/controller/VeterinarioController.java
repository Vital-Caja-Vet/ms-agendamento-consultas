package com.vitalcajavet.msagendamentoconsultas.controller;


import com.vitalcajavet.msagendamentoconsultas.model.Veterinario;
import com.vitalcajavet.msagendamentoconsultas.service.VeterinarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/veterinarios")
@CrossOrigin(origins = "*")
@Tag(name = "Veterinários", description = "Gerenciamento de veterinários")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService veterinarioService) {
        this.veterinarioService = veterinarioService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os veterinários")
    public ResponseEntity<List<Veterinario>> listarTodos() {
        List<Veterinario> veterinarios = veterinarioService.findAll();
        return ResponseEntity.ok(veterinarios);
    }

    @GetMapping("/ativos")
    @Operation(summary = "Listar veterinários ativos")
    public ResponseEntity<List<Veterinario>> listarAtivos() {
        List<Veterinario> veterinarios = veterinarioService.findAllAtivos();
        return ResponseEntity.ok(veterinarios);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar veterinário por ID")
    public ResponseEntity<Veterinario> buscarPorId(@PathVariable Long id) {
        return veterinarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar novo veterinário")
    public ResponseEntity<Veterinario> criar(@RequestBody Veterinario veterinario) {
        Veterinario veterinarioSalvo = veterinarioService.save(veterinario);
        return ResponseEntity.status(HttpStatus.CREATED).body(veterinarioSalvo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar veterinário")
    public ResponseEntity<Veterinario> atualizar(@PathVariable Long id, @RequestBody Veterinario veterinario) {
        try {
            Veterinario veterinarioAtualizado = veterinarioService.update(id, veterinario);
            return ResponseEntity.ok(veterinarioAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir veterinário")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (veterinarioService.existsById(id)) {
            veterinarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar veterinário")
    public ResponseEntity<Veterinario> desativar(@PathVariable Long id) {
        try {
            Veterinario veterinarioDesativado = veterinarioService.desativar(id);
            return ResponseEntity.ok(veterinarioDesativado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar veterinários por nome")
    public ResponseEntity<List<Veterinario>> buscarPorNome(@RequestParam String nome) {
        List<Veterinario> veterinarios = veterinarioService.findByNome(nome);
        return ResponseEntity.ok(veterinarios);
    }

    @GetMapping("/especialidade")
    @Operation(summary = "Buscar veterinários por especialidade")
    public ResponseEntity<List<Veterinario>> buscarPorEspecialidade(@RequestParam String especialidade) {
        List<Veterinario> veterinarios = veterinarioService.findByEspecialidade(especialidade);
        return ResponseEntity.ok(veterinarios);
    }

    @GetMapping("/estatisticas/contagem")
    @Operation(summary = "Contar veterinários ativos")
    public ResponseEntity<Long> contarAtivos() {
        long count = veterinarioService.countAtivos();
        return ResponseEntity.ok(count);
    }
}