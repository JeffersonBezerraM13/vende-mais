package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.task.TaskFilterDTO;
import br.com.vendemais.domain.dtos.task.TaskRequestDTO;
import br.com.vendemais.domain.dtos.task.TaskResponseDTO;
import br.com.vendemais.domain.entity.Lead;
import br.com.vendemais.domain.entity.Opportunity;
import br.com.vendemais.domain.entity.Task;
import br.com.vendemais.domain.entity.User;
import br.com.vendemais.domain.enums.TaskStatus;
import br.com.vendemais.repository.LeadRepository;
import br.com.vendemais.repository.OpportunityRepository;
import br.com.vendemais.repository.TaskRepository;
import br.com.vendemais.repository.specification.TaskSpecification;
import br.com.vendemais.security.SecurityUtils;
import br.com.vendemais.service.exceptions.BusinessRuleException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
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
    private final LeadRepository leadRepository;
    private final OpportunityRepository opportunityRepository;

    public TaskService(
            TaskRepository taskRepository,
            LeadRepository leadRepository,
            OpportunityRepository opportunityRepository
    ) {
        this.taskRepository = taskRepository;
        this.leadRepository = leadRepository;
        this.opportunityRepository = opportunityRepository;
    }

    /**
     * Retrieves tasks in pages, applying optional filters so CRM activity views can
     * search and narrow follow-ups directly by the backend.
     *
     * @param filter optional filtering criteria, such as search term, status,
     *               deadline condition and link type
     * @param pageable pagination and sorting instructions for the query
     * @return a page containing filtered task projections mapped to response DTOs
     */
    public Page<TaskResponseDTO> findAll(TaskFilterDTO filter, Pageable pageable) {
        Page<Task> tasksPage = taskRepository.findAll(
                TaskSpecification.withFilters(filter),
                pageable
        );

        return tasksPage.map(TaskResponseDTO::daEntidade);
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
     * to a lead or an opportunity. The task owner is resolved from the authenticated
     * user instead of trusting a user id sent by the client.
     *
     * @param dto payload describing the task to schedule
     * @return the persisted task mapped to the API response DTO
     * @throws ObjectNotFoundException if the linked lead or opportunity does not exist
     * @throws BusinessRuleException if the task is not linked to a lead or opportunity
     */
    @Transactional
    public TaskResponseDTO create(TaskRequestDTO dto) {
        User loggedUser = SecurityUtils.getLoggedUser();
        Lead lead = resolveLead(dto.leadId());
        Opportunity opportunity = resolveOpportunity(dto.opportunityId());

        ensureTaskHasExactlyOneCommercialLink(lead, opportunity);

        Task task = buildTask(dto, loggedUser, lead, opportunity);

        return TaskResponseDTO.daEntidade(taskRepository.save(task));
    }

    /**
     * Updates a task while revalidating its linkage to the commercial records it
     * supports. The task owner is refreshed from the authenticated user.
     *
     * @param id identifier of the task being updated
     * @param dto payload containing the revised task data
     * @return the persisted task mapped to the API response DTO
     * @throws ObjectNotFoundException if the task or one of its referenced records does not exist
     * @throws BusinessRuleException if the task is left without a lead or opportunity linkage
     */
    @Transactional
    public TaskResponseDTO update(Long id, TaskRequestDTO dto) {
        Task task = findTaskById(id);
        User loggedUser = SecurityUtils.getLoggedUser();
        Lead lead = resolveLead(dto.leadId());
        Opportunity opportunity = resolveOpportunity(dto.opportunityId());

        ensureTaskHasExactlyOneCommercialLink(lead, opportunity);
        updateTaskData(task, dto, loggedUser, lead, opportunity);

        return TaskResponseDTO.daEntidade(taskRepository.save(task));
    }

    /**
     * Deletes a task when the follow-up is no longer relevant to the CRM process.
     *
     * @param id identifier of the task to delete
     * @throws ObjectNotFoundException if the task does not exist
     */
    @Transactional
    public void delete(Long id) {
        Task task = findTaskById(id);
        taskRepository.delete(task);
    }

    private Task buildTask(
            TaskRequestDTO dto,
            User user,
            Lead lead,
            Opportunity opportunity
    ) {
        return new Task(
                user,
                dto.title(),
                dto.description(),
                resolveTaskStatus(dto.taskStatus()),
                dto.dueDate(),
                lead,
                opportunity
        );
    }

    private void updateTaskData(
            Task task,
            TaskRequestDTO dto,
            User user,
            Lead lead,
            Opportunity opportunity
    ) {
        task.setUser(user);
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setStatus(resolveTaskStatus(dto.taskStatus()));
        task.setDueDate(dto.dueDate());
        task.setLead(lead);
        task.setOpportunity(opportunity);
        task.setUpdatedAt(LocalDate.now());
    }

    private Lead resolveLead(Long leadId) {
        if (leadId == null) {
            return null;
        }

        return leadRepository.findById(leadId)
                .orElseThrow(() -> new ObjectNotFoundException("Lead não encontrado. ID: " + leadId));
    }

    private Opportunity resolveOpportunity(Long opportunityId) {
        if (opportunityId == null) {
            return null;
        }

        return opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ObjectNotFoundException("Oportunidade não encontrada. ID: " + opportunityId));
    }

    private void ensureTaskHasExactlyOneCommercialLink(Lead lead, Opportunity opportunity) {
        if (lead == null && opportunity == null) {
            throw new BusinessRuleException("Toda tarefa deve estar vinculada a um Lead ou a uma Oportunidade.");
        }

        if (lead != null && opportunity != null) {
            throw new BusinessRuleException("A tarefa não pode estar vinculada a um Lead e a uma Oportunidade ao mesmo tempo.");
        }
    }

    private TaskStatus resolveTaskStatus(TaskStatus taskStatus) {
        return taskStatus != null ? taskStatus : TaskStatus.PENDING;
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Task não encontrada. ID: " + id));
    }
}