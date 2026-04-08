package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.task.TaskRequestDTO;
import br.com.vendemais.domain.dtos.task.TaskResponseDTO;
import br.com.vendemais.domain.entity.Lead;
import br.com.vendemais.domain.entity.Opportunity;
import br.com.vendemais.domain.entity.Pipeline;
import br.com.vendemais.domain.entity.Stage;
import br.com.vendemais.domain.entity.Task;
import br.com.vendemais.domain.enums.EntryMethod;
import br.com.vendemais.domain.enums.LeadSource;
import br.com.vendemais.domain.enums.PersonType;
import br.com.vendemais.domain.enums.Solution;
import br.com.vendemais.domain.enums.TaskStatus;
import br.com.vendemais.repository.LeadRepository;
import br.com.vendemais.repository.OpportunityRepository;
import br.com.vendemais.repository.TaskRepository;
import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static br.com.vendemais.support.TestReflectionUtils.setId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private OpportunityRepository opportunityRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(taskService, "leadRepository", leadRepository);
        ReflectionTestUtils.setField(taskService, "opportunityRepository", opportunityRepository);
    }

    @Test
    void createShouldPersistTaskWhenLeadLinkExists() {
        Lead lead = lead(1L);
        TaskRequestDTO dto = new TaskRequestDTO(
                "Ligar para cliente",
                "Confirmar proposta",
                TaskStatus.PENDING,
                LocalDate.now().plusDays(2),
                lead.getId(),
                null
        );

        when(leadRepository.findById(lead.getId())).thenReturn(Optional.of(lead));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task saved = invocation.getArgument(0);
            setId(saved, 88L);
            return saved;
        });

        TaskResponseDTO response = taskService.create(dto);

        assertThat(response.id()).isEqualTo(88L);
        assertThat(response.leadId()).isEqualTo(lead.getId());
        assertThat(response.opportunityId()).isNull();
        assertThat(response.taskStatus()).isEqualTo(TaskStatus.PENDING);

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createShouldPersistTaskWhenOpportunityLinkExists() {
        Opportunity opportunity = opportunity(5L);
        TaskRequestDTO dto = new TaskRequestDTO(
                "Enviar contrato",
                null,
                TaskStatus.COMPLETED,
                LocalDate.now().plusDays(5),
                null,
                opportunity.getId()
        );

        when(opportunityRepository.findById(opportunity.getId())).thenReturn(Optional.of(opportunity));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task saved = invocation.getArgument(0);
            setId(saved, 89L);
            return saved;
        });

        TaskResponseDTO response = taskService.create(dto);

        assertThat(response.id()).isEqualTo(89L);
        assertThat(response.leadId()).isNull();
        assertThat(response.opportunityId()).isEqualTo(opportunity.getId());
        assertThat(response.taskStatus()).isEqualTo(TaskStatus.COMPLETED);
    }

    @Test
    void createShouldRequireLeadOrOpportunityLink() {
        TaskRequestDTO dto = new TaskRequestDTO(
                "Tarefa sem vinculo",
                null,
                TaskStatus.PENDING,
                LocalDate.now().plusDays(1),
                null,
                null
        );

        assertThatThrownBy(() -> taskService.create(dto))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Lead ou a uma Oportunidade");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateShouldKeepCurrentStatusWhenRequestStatusIsNull() {
        Lead currentLead = lead(1L);
        Task existingTask = new Task(
                "Tarefa atual",
                "Descricao",
                TaskStatus.PENDING,
                LocalDate.now().plusDays(3),
                currentLead,
                null
        );
        setId(existingTask, 50L);

        Lead newLead = lead(2L);
        TaskRequestDTO dto = new TaskRequestDTO(
                "Tarefa atualizada",
                "Nova descricao",
                null,
                LocalDate.now().plusDays(7),
                newLead.getId(),
                null
        );

        when(taskRepository.findById(50L)).thenReturn(Optional.of(existingTask));
        when(leadRepository.findById(newLead.getId())).thenReturn(Optional.of(newLead));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        TaskResponseDTO response = taskService.update(50L, dto);

        assertThat(response.taskStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(response.leadId()).isEqualTo(newLead.getId());
        assertThat(existingTask.getUpdatedAt()).isEqualTo(LocalDate.now());
        verify(taskRepository).save(existingTask);
    }

    private Lead lead(Long id) {
        Lead lead = new Lead(
                "Lead Teste",
                "11999999999",
                "lead" + id + "@example.com",
                PersonType.INDIVIDUAL,
                null,
                Solution.SELF_STORAGE,
                LeadSource.WHATSAPP,
                EntryMethod.MANUAL,
                null
        );
        setId(lead, id);
        return lead;
    }

    private Opportunity opportunity(Long id) {
        Lead lead = lead(100L);
        Pipeline pipeline = new Pipeline("Pipeline");
        setId(pipeline, 10L);
        Stage stage = new Stage("Contato", "CONTATO", 1, pipeline);
        setId(stage, 20L);
        Opportunity opportunity = new Opportunity(
                lead,
                "Oportunidade",
                Solution.COWORKING,
                null,
                stage,
                LocalDate.now().plusDays(10),
                null
        );
        setId(opportunity, id);
        return opportunity;
    }
}
