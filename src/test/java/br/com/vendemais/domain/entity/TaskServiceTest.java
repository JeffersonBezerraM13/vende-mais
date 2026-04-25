package br.com.vendemais.domain.entity;

import br.com.vendemais.domain.enums.EntryMethod;
import br.com.vendemais.domain.enums.LeadSource;
import br.com.vendemais.domain.enums.PersonType;
import br.com.vendemais.domain.enums.Solution;
import br.com.vendemais.domain.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTest {

    private User userAdmin;
    private Lead lead;
    private Opportunity opportunity;

    @BeforeEach
    void setUp() {
        userAdmin = new User(
                "Admin Test",
                "admin@test.com",
                "encoded-password"
        );

        lead = new Lead(
                "João Silva",
                "83999999999",
                "joao@email.com",
                PersonType.INDIVIDUAL,
                null,
                Solution.COWORKING,
                LeadSource.WHATSAPP,
                EntryMethod.MANUAL,
                "Lead de teste"
        );

        Pipeline pipeline = new Pipeline("Funil de Teste");

        Stage stage = new Stage(
                "Contato Inicial",
                "CONTATO_INICIAL",
                1,
                pipeline
        );

        opportunity = new Opportunity(
                lead,
                "Venda de Software",
                Solution.COWORKING,
                new BigDecimal("1500.00"),
                stage,
                LocalDate.now().plusDays(10),
                "Oportunidade de teste"
        );
    }

    @Test
    @DisplayName("Deve criar uma task vinculada corretamente a um lead e a um usuário")
    void shouldCreateTaskWithLeadAndUser() {
        String title = "Ligar para Lead";
        String description = "Qualificação inicial";
        LocalDate dueDate = LocalDate.now().plusDays(1);

        Task task = new Task(
                userAdmin,
                title,
                description,
                TaskStatus.PENDING,
                dueDate,
                lead,
                null
        );

        assertThat(task.getUser()).isEqualTo(userAdmin);
        assertThat(task.getLead()).isEqualTo(lead);
        assertThat(task.getOpportunity()).isNull();
        assertThat(task.getTitle()).isEqualTo(title);
        assertThat(task.getDescription()).isEqualTo(description);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(task.getDueDate()).isEqualTo(dueDate);
    }

    @Test
    @DisplayName("Deve criar uma task vinculada corretamente a uma oportunidade")
    void shouldCreateTaskWithOpportunity() {
        LocalDate dueDate = LocalDate.now().plusDays(2);

        Task task = new Task(
                userAdmin,
                "Enviar proposta",
                "Draft da proposta comercial",
                TaskStatus.PENDING,
                dueDate,
                null,
                opportunity
        );

        assertThat(task.getUser()).isEqualTo(userAdmin);
        assertThat(task.getOpportunity()).isEqualTo(opportunity);
        assertThat(task.getLead()).isNull();
        assertThat(task.getTitle()).isEqualTo("Enviar proposta");
        assertThat(task.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(task.getDueDate()).isEqualTo(dueDate);
    }

    @Test
    @DisplayName("Deve permitir a atualização do status da tarefa")
    void shouldUpdateTaskStatus() {
        Task task = new Task(
                userAdmin,
                "Teste",
                "Descrição",
                TaskStatus.PENDING,
                LocalDate.now().plusDays(1),
                lead,
                null
        );

        task.setStatus(TaskStatus.COMPLETED);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.COMPLETED);
    }

    @Test
    @DisplayName("Deve permitir atualizar dados básicos da tarefa")
    void shouldUpdateTaskBasicData() {
        Task task = new Task(
                userAdmin,
                "Título antigo",
                "Descrição antiga",
                TaskStatus.PENDING,
                LocalDate.now().plusDays(1),
                lead,
                null
        );

        LocalDate newDueDate = LocalDate.now().plusDays(5);

        task.setTitle("Título atualizado");
        task.setDescription("Descrição atualizada");
        task.setDueDate(newDueDate);

        assertThat(task.getTitle()).isEqualTo("Título atualizado");
        assertThat(task.getDescription()).isEqualTo("Descrição atualizada");
        assertThat(task.getDueDate()).isEqualTo(newDueDate);
    }

    @Test
    @DisplayName("Deve permitir trocar o vínculo de lead para oportunidade")
    void shouldAllowChangingTaskLinkFromLeadToOpportunity() {
        Task task = new Task(
                userAdmin,
                "Follow-up",
                "Contato inicial",
                TaskStatus.PENDING,
                LocalDate.now().plusDays(1),
                lead,
                null
        );

        task.setLead(null);
        task.setOpportunity(opportunity);

        assertThat(task.getLead()).isNull();
        assertThat(task.getOpportunity()).isEqualTo(opportunity);
    }
}