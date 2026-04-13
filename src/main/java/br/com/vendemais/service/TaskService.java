package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.task.TaskRequestDTO;
import br.com.vendemais.domain.dtos.task.TaskResponseDTO;
import br.com.vendemais.domain.entity.Lead;
import br.com.vendemais.domain.entity.Opportunity;
import br.com.vendemais.domain.entity.Task;
import br.com.vendemais.domain.entity.User;
import br.com.vendemais.repository.LeadRepository;
import br.com.vendemais.repository.OpportunityRepository;
import br.com.vendemais.repository.TaskRepository;
import br.com.vendemais.security.SecurityUtils;
import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Manages follow-up tasks linked to leads or opportunities so execution remains
 * aligned with the sales process.
 */
@Service
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    };

    /**
     * Retrieves tasks in pages so CRM activity views can be rendered efficiently.
     *
     * @param pageable pagination and sorting instructions for the query
     * @return a page containing task projections mapped to response DTOs
     */
    public Page<TaskResponseDTO> findAll(Pageable pageable) {
        Page<Task> paginaDeTasks = taskRepository.findAll(pageable);

        return paginaDeTasks.map(TaskResponseDTO::daEntidade);
    }

    /**
     * Loads a task by identifier so users can inspect its schedule, status, and
     * linked commercial record.
     *
     * @param id identifier of the task to retrieve
     * @return the requested task mapped to the API response DTO
     * @throws ObjectNotFoundException if the task does not exist
     */
    public TaskResponseDTO findById(Long id) {
        Task task = findTaskById(id);
        return TaskResponseDTO.daEntidade(task);
    }

    /**
     * Creates a task and enforces the CRM rule that every activity must be linked
     * to a lead or an opportunity.
     *
     * @param dto payload describing the task to schedule
     * @return the persisted task mapped to the API response DTO
     * @throws DataIntegrityViolationException if the linked lead or opportunity is invalid or absent
     */
    @Transactional
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

        User user = SecurityUtils.getLoggedUser();

        Task task = new Task(
                user,
                dto.title(),
                dto.description(),
                dto.taskStatus(), // Ou defina como PENDING por padrão, dependendo de como você fez
                dto.dueDate(),
                lead,
                opportunity
        );

        return TaskResponseDTO.daEntidade(taskRepository.save(task));
    }

    /**
     * Updates a task while revalidating its linkage to the commercial records it
     * supports.
     *
     * @param id identifier of the task being updated
     * @param dto payload containing the revised task data
     * @return the persisted task mapped to the API response DTO
     * @throws ObjectNotFoundException if the task or one of its referenced records does not exist
     * @throws DataIntegrityViolationException if the task is left without a lead or opportunity linkage
     */
    @Transactional
    public TaskResponseDTO update(Long id, TaskRequestDTO dto) {
        User user = SecurityUtils.getLoggedUser();

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

        task.setUser(user);
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

    /**
     * Deletes a task when the follow-up is no longer relevant to the CRM process.
     *
     * @param id identifier of the task to delete
     * @throws ObjectNotFoundException if the task does not exist
     */
    @Transactional
    public void delete(Long id){
        Task task = findTaskById(id);
        taskRepository.delete(task);
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Task não encontrado. ID:" +id));
    }
}
