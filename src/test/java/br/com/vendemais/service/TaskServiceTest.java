package br.com.vendemais.domain.entity;

import br.com.vendemais.domain.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTest {

    private User userAdmin;
    private Lead lead;
    private Opportunity opportunity;

    @BeforeEach
    void setUp() {
        // Criando os objetos base para os testes
        userAdmin = new User(); // Supondo que você tenha um construtor ou setters
        userAdmin.setName("Admin Test");

        lead = new Lead();
        lead.setName("João Silva");

        opportunity = new Opportunity();
        opportunity.setTitle("Venda de Software");
    }

    @Test
    @DisplayName("Deve criar uma Task vinculada corretamente a um Lead e um Usuário")
    void shouldCreateTaskWithLeadAndUser() {
        // Arrange
        String title = "Ligar para Lead";
        String desc = "Qualificação inicial";
        LocalDate dueDate = LocalDate.now().plusDays(1);

        // Act
        Task task = new Task(userAdmin, title, desc, TaskStatus.PENDING, dueDate, lead, null);

        // Assert
        assertThat(task.getUser()).isEqualTo(userAdmin);
        assertThat(task.getLead()).isEqualTo(lead);
        assertThat(task.getOpportunity()).isNull();
        assertThat(task.getTitle()).isEqualTo(title);
        assertThat(task.getCreatedAt()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Deve criar uma Task vinculada corretamente a uma Oportunidade")
    void shouldCreateTaskWithOpportunity() {
        // Act
        Task task = new Task(
                userAdmin,
                "Enviar Proposta",
                "Draft da proposta comercial",
                TaskStatus.PENDING,
                LocalDate.now().plusDays(2),
                null,
                opportunity
        );

        // Assert
        assertThat(task.getOpportunity()).isEqualTo(opportunity);
        assertThat(task.getLead()).isNull();
        assertThat(task.getUser()).isEqualTo(userAdmin);
    }

    @Test
    @DisplayName("Deve garantir que createdAt seja inicializado no construtor")
    void shouldInitializeCreatedAt() {
        Task task = new Task(userAdmin, "Teste", "Desc", TaskStatus.PENDING, LocalDate.now(), null, null);

        assertThat(task.getCreatedAt()).isNotNull();
        assertThat(task.getCreatedAt()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Deve permitir a atualização do status da tarefa")
    void shouldUpdateTaskStatus() {
        Task task = new Task(userAdmin, "Teste", "Desc", TaskStatus.PENDING, LocalDate.now(), null, null);

        task.setStatus(TaskStatus.COMPLETED);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.COMPLETED);
    }
}