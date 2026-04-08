package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.task.TaskRequestDTO;
import br.com.vendemais.domain.dtos.task.TaskResponseDTO;
import br.com.vendemais.domain.entity.Lead;
import br.com.vendemais.domain.entity.Opportunity;
import br.com.vendemais.domain.entity.Task;
import br.com.vendemais.repository.LeadRepository;
import br.com.vendemais.repository.OpportunityRepository;
import br.com.vendemais.repository.TaskRepository;
import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    };

    public Page<TaskResponseDTO> findAll(Pageable pageable) {
        Page<Task> paginaDeTasks = taskRepository.findAll(pageable);

        return paginaDeTasks.map(TaskResponseDTO::daEntidade);
    }

    public TaskResponseDTO findById(Long id) {
        Task task = findTaskById(id);
        return TaskResponseDTO.daEntidade(task);
    }

    public TaskResponseDTO create(TaskRequestDTO dto) {
        Lead lead = null;
        Opportunity opportunity = null;

        if (dto.leadId() != null) {
            lead = leadRepository.findById(dto.leadId())
                    .orElseThrow(() -> new DataIntegrityViolationException("Lead não encontrado"));
        }

        if (dto.opportunityId() != null) {
            opportunity = opportunityRepository.findById(dto.opportunityId())
                    .orElseThrow(() -> new DataIntegrityViolationException("Oportunidade não encontrada"));
        }

        if (lead == null && opportunity == null) {
            throw new DataIntegrityViolationException("Toda tarefa deve estar vinculada a um Lead ou a uma Oportunidade.");
        }

        Task task = new Task(
                dto.title(),
                dto.description(),
                dto.taskStatus(), // Ou defina como PENDING por padrão, dependendo de como você fez
                dto.dueDate(),
                lead,
                opportunity
        );

        return TaskResponseDTO.daEntidade(taskRepository.save(task));
    }

    public TaskResponseDTO update(Long id, TaskRequestDTO dto) {
        Task task = findTaskById(id);

        Lead lead = null;
        Opportunity op = null;

        if (dto.leadId() != null) {
            lead = leadRepository.findById(dto.leadId())
                    .orElseThrow(() -> new ObjectNotFoundException("Lead não encontrado"));
        }

        if (dto.opportunityId() != null) {
            op = opportunityRepository.findById(dto.opportunityId())
                    .orElseThrow(() -> new ObjectNotFoundException("Oportunidade não encontrada"));
        }

        if (lead == null && op == null) {
            throw new DataIntegrityViolationException("Toda tarefa deve estar vinculada a um Lead ou a uma Oportunidade.");
        }

        task.setTitle(dto.title());
        task.setDescription(dto.description());
        if (dto.taskStatus() != null) {
            task.setStatus(dto.taskStatus());
        }
        task.setDueDate(dto.dueDate());
        task.setLead(lead);
        task.setOpportunity(op);
        task.setUpdatedAt(LocalDate.now());

        return TaskResponseDTO.daEntidade(taskRepository.save(task));
    }

    public void delete(Long id){
        Task task = findTaskById(id);
        taskRepository.delete(task);
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Task não encontrado. ID:" +id));
    }
}
