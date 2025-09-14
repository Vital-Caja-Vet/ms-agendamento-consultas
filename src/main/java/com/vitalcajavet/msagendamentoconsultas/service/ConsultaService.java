package com.vitalcajavet.msagendamentoconsultas.service;

import com.vitalcajavet.msagendamentoconsultas.config.HorarioComercialProperties;
import com.vitalcajavet.msagendamentoconsultas.dto.ConsultaRequestDTO;
import com.vitalcajavet.msagendamentoconsultas.dto.HorarioDisponivelRequestDTO;
import com.vitalcajavet.msagendamentoconsultas.dto.HorarioDisponivelResponseDTO;
import com.vitalcajavet.msagendamentoconsultas.model.Consulta;
import com.vitalcajavet.msagendamentoconsultas.model.Veterinario;
import com.vitalcajavet.msagendamentoconsultas.model.enums.StatusConsulta;
import com.vitalcajavet.msagendamentoconsultas.model.enums.TipoConsulta;
import com.vitalcajavet.msagendamentoconsultas.repository.ConsultaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final VeterinarioService veterinarioService;
    private final HorarioComercialProperties horarioComercialProperties;

    public ConsultaService(ConsultaRepository consultaRepository,
                           VeterinarioService veterinarioService,
                           HorarioComercialProperties horarioComercialProperties) {
        this.consultaRepository = consultaRepository;
        this.veterinarioService = veterinarioService;
        this.horarioComercialProperties = horarioComercialProperties;
    }

    @Transactional
    public Consulta agendarConsulta(ConsultaRequestDTO requestDTO) {
        validarDadosAgendamento(requestDTO);

        Consulta consulta = new Consulta();
        consulta.setAnimalId(requestDTO.getAnimalId());
        consulta.setVeterinarioId(requestDTO.getVeterinarioId());
        consulta.setDataHora(requestDTO.getDataHora());
        consulta.setTipo(requestDTO.getTipo());
        consulta.setStatus(StatusConsulta.AGENDADA);

        return consultaRepository.save(consulta);
    }

    private void validarDadosAgendamento(ConsultaRequestDTO requestDTO) {
        Veterinario veterinario = veterinarioService.findById(requestDTO.getVeterinarioId())
                .orElseThrow(() -> new RuntimeException("Veterinário não encontrado"));

        if (Boolean.FALSE.equals(veterinario.getAtivo())) {
            throw new RuntimeException("Veterinário não está ativo");
        }

        if (requestDTO.getDataHora().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Não é possível agendar consultas no passado");
        }

        if (consultaRepository.existsByVeterinarioIdAndDataHora(
                requestDTO.getVeterinarioId(), requestDTO.getDataHora())) {
            throw new RuntimeException("Já existe uma consulta agendada para este veterinário no mesmo horário");
        }

        validarHorarioComercial(requestDTO.getDataHora());
    }

    private void validarHorarioComercial(LocalDateTime dataHora) {
        LocalTime horario = dataHora.toLocalTime();
        LocalTime inicioExpediente = LocalTime.of(horarioComercialProperties.getInicio(), 0);
        LocalTime fimExpediente = LocalTime.of(horarioComercialProperties.getFim(), 0);

        if (horario.isBefore(inicioExpediente) || horario.isAfter(fimExpediente)) {
            throw new RuntimeException("Horário fora do expediente comercial ("
                    + horarioComercialProperties.getInicio() + ":00 às "
                    + horarioComercialProperties.getFim() + ":00)");
        }

        int minutos = horario.getMinute();
        if (minutos % horarioComercialProperties.getIntervaloMinutos() != 0) {
            throw new RuntimeException("Os horários devem ser marcados em intervalos de "
                    + horarioComercialProperties.getIntervaloMinutos() + " minutos (ex: 08:00, 08:30, 09:00)");
        }
    }

    @Transactional
    public Consulta cancelarConsulta(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        if (!consultaRepository.podeSerCancelada(id, LocalDateTime.now())) {
            throw new RuntimeException("Cancelamento permitido apenas com "
                    + horarioComercialProperties.getHorasMinimasCancelamento() + " horas de antecedência");
        }

        if (consulta.getStatus() == StatusConsulta.CANCELADA) {
            throw new RuntimeException("Consulta já está cancelada");
        }

        if (consulta.getStatus() == StatusConsulta.REALIZADA) {
            throw new RuntimeException("Não é possível cancelar uma consulta já realizada");
        }

        consulta.setStatus(StatusConsulta.CANCELADA);
        return consultaRepository.save(consulta);
    }

    public List<Consulta> listarTodasConsultas() {
        return consultaRepository.findAll();
    }

    public Optional<Consulta> buscarPorId(Long id) {
        return consultaRepository.findById(id);
    }

    public List<Consulta> listarConsultasPorVeterinario(Long veterinarioId) {
        return consultaRepository.findByVeterinarioId(veterinarioId);
    }

    public List<Consulta> listarConsultasPorAnimal(Long animalId) {
        return consultaRepository.findByAnimalId(animalId);
    }

    public List<Consulta> listarConsultasPorStatus(StatusConsulta status) {
        return consultaRepository.findByStatus(status);
    }

    public List<Consulta> listarConsultasPorTipo(TipoConsulta tipo) {
        return consultaRepository.findByTipo(tipo);
    }

    public List<Consulta> listarConsultasFuturas() {
        return consultaRepository.findConsultasFuturas(LocalDateTime.now());
    }

    public List<Consulta> listarConsultasDeHoje() {
        return consultaRepository.findConsultasDeHoje();
    }

    public HorarioDisponivelResponseDTO listarHorariosDisponiveis(HorarioDisponivelRequestDTO requestDTO) {
        Veterinario veterinario = veterinarioService.findById(requestDTO.getVeterinarioId())
                .orElseThrow(() -> new RuntimeException("Veterinário não encontrado"));

        if (Boolean.FALSE.equals(veterinario.getAtivo())) {
            throw new RuntimeException("Veterinário não está ativo");
        }

        LocalDate data = requestDTO.getData();

        if (data.isBefore(LocalDate.now())) {
            throw new RuntimeException("Não é possível consultar horários para datas passadas");
        }

        List<LocalDateTime> horariosOcupados = consultaRepository
                .findHorariosOcupadosByVeterinarioIdAndData(requestDTO.getVeterinarioId(),
                        data.atStartOfDay());

        List<LocalDateTime> todosHorarios = gerarHorariosDoDia(data);

        List<LocalDateTime> horariosDisponiveis = todosHorarios.stream()
                .filter(horario -> !horariosOcupados.contains(horario))
                .filter(horario -> !horario.isBefore(LocalDateTime.now())) // Não mostrar horários passados se for hoje
                .toList();

        HorarioDisponivelResponseDTO response = new HorarioDisponivelResponseDTO();
        response.setVeterinarioId(requestDTO.getVeterinarioId());
        response.setNomeVeterinario(veterinario.getNome());
        response.setHorariosDisponiveis(horariosDisponiveis);
        response.setHorariosOcupados(horariosOcupados);

        return response;
    }

    private List<LocalDateTime> gerarHorariosDoDia(LocalDate data) {
        List<LocalDateTime> horarios = new ArrayList<>();
        LocalDateTime horarioAtual = data.atTime(horarioComercialProperties.getInicio(), 0);
        LocalDateTime fimDoDia = data.atTime(horarioComercialProperties.getFim(), 0);

        while (horarioAtual.isBefore(fimDoDia)) {
            horarios.add(horarioAtual);
            horarioAtual = horarioAtual.plusMinutes(horarioComercialProperties.getIntervaloMinutos());
        }

        return horarios;
    }

    @Transactional
    public Consulta atualizarStatus(Long id, StatusConsulta novoStatus) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        if (novoStatus == StatusConsulta.CANCELADA) {
            throw new RuntimeException("Use o endpoint específico para cancelamento");
        }

        if (consulta.getStatus() == StatusConsulta.CANCELADA && novoStatus != StatusConsulta.CANCELADA) {
            throw new RuntimeException("Não é possível reativar uma consulta cancelada");
        }

        consulta.setStatus(novoStatus);
        return consultaRepository.save(consulta);
    }

    @Transactional
    public Consulta atualizarConsulta(Long id, ConsultaRequestDTO requestDTO) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        if (consulta.getStatus() == StatusConsulta.REALIZADA) {
            throw new RuntimeException("Não é possível alterar uma consulta já realizada");
        }

        if (consulta.getStatus() == StatusConsulta.CANCELADA) {
            throw new RuntimeException("Não é possível alterar uma consulta cancelada");
        }

        validarDadosAgendamento(requestDTO);

        consulta.setAnimalId(requestDTO.getAnimalId());
        consulta.setVeterinarioId(requestDTO.getVeterinarioId());
        consulta.setDataHora(requestDTO.getDataHora());
        consulta.setTipo(requestDTO.getTipo());

        return consultaRepository.save(consulta);
    }

    public List<Consulta> listarConsultasPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return consultaRepository.findByVeterinarioIdAndPeriodo(null, inicio, fim);
    }

    public List<Consulta> listarConsultasPorVeterinarioEPeriodo(Long veterinarioId, LocalDateTime inicio, LocalDateTime fim) {
        return consultaRepository.findByVeterinarioIdAndPeriodo(veterinarioId, inicio, fim);
    }

    public boolean verificarDisponibilidade(Long veterinarioId, LocalDateTime dataHora) {
        return !consultaRepository.existsByVeterinarioIdAndDataHora(veterinarioId, dataHora);
    }
}
