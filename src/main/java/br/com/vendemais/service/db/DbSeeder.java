package br.com.vendemais.service.db;

import br.com.vendemais.domain.entity.Lead;
import br.com.vendemais.domain.entity.Opportunity;
import br.com.vendemais.domain.entity.Pipeline;
import br.com.vendemais.domain.entity.Stage;
import br.com.vendemais.domain.entity.Task;
import br.com.vendemais.domain.entity.User;
import br.com.vendemais.domain.enums.EntryMethod;
import br.com.vendemais.domain.enums.LeadSource;
import br.com.vendemais.domain.enums.PersonType;
import br.com.vendemais.domain.enums.Role;
import br.com.vendemais.domain.enums.Solution;
import br.com.vendemais.domain.enums.TaskStatus;
import br.com.vendemais.repository.LeadRepository;
import br.com.vendemais.repository.OpportunityRepository;
import br.com.vendemais.repository.PipelineRepository;
import br.com.vendemais.repository.StageRepository;
import br.com.vendemais.repository.TaskRepository;
import br.com.vendemais.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DbSeeder {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final PipelineRepository pipelineRepository;
    private final StageRepository stageRepository;
    private final OpportunityRepository opportunityRepository;
    private final TaskRepository taskRepository;
    private final BCryptPasswordEncoder encoder;

    public DbSeeder(
            LeadRepository leadRepository,
            UserRepository userRepository,
            PipelineRepository pipelineRepository,
            StageRepository stageRepository,
            OpportunityRepository opportunityRepository,
            TaskRepository taskRepository,
            BCryptPasswordEncoder encoder
    ) {
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
        this.pipelineRepository = pipelineRepository;
        this.stageRepository = stageRepository;
        this.opportunityRepository = opportunityRepository;
        this.taskRepository = taskRepository;
        this.encoder = encoder;
    }

    @Transactional
    public void populateDb() {
        clear();

        User userAdmin = createAdminUser();
        User user1 = createUser("Nikola Tesla", "tesla@gmail.com");
        User user2 = createUser("Marie Curie", "curie@gmail.com");

        userRepository.saveAll(List.of(userAdmin, user1, user2));

        Lead lead1 = new Lead(
                "Ana Grey",
                "83998765432",
                "ana@gmail.com",
                PersonType.INDIVIDUAL,
                null,
                Solution.SELF_STORAGE,
                LeadSource.WHATSAPP,
                EntryMethod.MANUAL,
                "Lead entrou em contato via WhatsApp buscando orçamento para Self Storage."
        );

        Lead lead2 = new Lead(
                "Bob Blue",
                "83912345678",
                "bob@gmail.com",
                PersonType.COMPANY,
                "Blue Corp",
                Solution.COWORKING,
                LeadSource.PHONE_CALL,
                EntryMethod.MANUAL,
                "O Bob ligou procurando opções de Coworking para a equipe comercial da Blue Corp. Eles operam 100% remoto hoje, mas precisam de um espaço fixo com acesso à sala de reunião para receber clientes presencialmente duas vezes por semana. Pediu orçamento para 4 posições."
        );

        Lead lead3 = new Lead(
                "Jane Green",
                "83911112222",
                "jane@gmail.com",
                PersonType.COMPANY,
                "Green Inc",
                Solution.FISCAL_ADDRESS,
                LeadSource.SITE,
                EntryMethod.INTEGRATION,
                "Lead capturado via formulário do site. A Green Inc é uma startup de São Paulo e está abrindo um CNPJ filial na Paraíba. Precisam do serviço de Endereço Fiscal e Comercial para registro na junta comercial e gestão de correspondências."
        );

        Lead lead4 = new Lead(
                "Carlos Orange",
                "83922223333",
                "carlos@gmail.com",
                PersonType.COMPANY,
                "Orange Tech",
                Solution.COMMERCIAL_ADDRESS,
                LeadSource.REFERRAL,
                EntryMethod.MANUAL,
                "Lead indicado por cliente ativo. Demonstrou interesse em endereço comercial, mas ainda está comparando fornecedores."
        );

        Lead lead5 = new Lead(
                "Marina Marketing",
                "83933334444",
                "marina.marketing@example.com",
                PersonType.INDIVIDUAL,
                null,
                Solution.NOT_SPECIFIED,
                LeadSource.MARKETING_INTEGRATION,
                EntryMethod.INTEGRATION,
                "Lead importado automaticamente por integração externa de marketing. A solução de interesse ainda não foi qualificada pelo time comercial."
        );

        leadRepository.saveAll(List.of(lead1, lead2, lead3, lead4, lead5));

        Pipeline commercialPipeline = new Pipeline("Funil Comercial");
        Pipeline coworkingPipeline = new Pipeline("Locação de Espaços (Coworking)");

        pipelineRepository.saveAll(List.of(commercialPipeline, coworkingPipeline));

        Stage commercialNewLead = createStage(commercialPipeline, "Novo lead", "NOVO_LEAD", 1);
        Stage commercialInitialContact = createStage(commercialPipeline, "Contato inicial", "CONTATO_INICIAL", 2);
        Stage commercialQualification = createStage(commercialPipeline, "Qualificação", "QUALIFICACAO", 3);
        Stage commercialProposalSent = createStage(commercialPipeline, "Proposta enviada", "PROPOSTA_ENVIADA", 4);

        Stage coworkingInitialContact = createStage(coworkingPipeline, "Contato inicial", "CONTATO_INICIAL", 1);
        Stage coworkingTourScheduled = createStage(coworkingPipeline, "Visita agendada (Tour)", "VISITA_AGENDADA", 2);
        Stage coworkingTourDone = createStage(coworkingPipeline, "Visita realizada", "VISITA_REALIZADA", 3);
        Stage coworkingProposalSent = createStage(coworkingPipeline, "Proposta enviada", "PROPOSTA_ENVIADA", 4);
        Stage coworkingContractReview = createStage(coworkingPipeline, "Revisão de contrato", "REVISAO_CONTRATO", 5);

        stageRepository.saveAll(List.of(
                commercialNewLead,
                commercialInitialContact,
                commercialQualification,
                commercialProposalSent,
                coworkingInitialContact,
                coworkingTourScheduled,
                coworkingTourDone,
                coworkingProposalSent,
                coworkingContractReview
        ));

        Opportunity openOpportunity1 = new Opportunity(
                lead1,
                "Self Storage - Ana",
                Solution.SELF_STORAGE,
                new BigDecimal("1000.00"),
                commercialNewLead,
                LocalDate.now().plusDays(14),
                "Oportunidade aberta para contratação de Self Storage."
        );

        Opportunity openOpportunity2 = new Opportunity(
                lead2,
                "Coworking - Bob",
                Solution.COWORKING,
                new BigDecimal("4500.00"),
                coworkingProposalSent,
                LocalDate.now().plusDays(15),
                "Oportunidade em negociação para locação de 4 posições de coworking."
        );

        Opportunity wonOpportunity = new Opportunity(
                lead3,
                "Endereço Fiscal - Jane",
                Solution.FISCAL_ADDRESS,
                new BigDecimal("800.00"),
                commercialProposalSent,
                LocalDate.now().plusDays(7),
                "Oportunidade fechada como ganha para contratação de endereço fiscal."
        );
        wonOpportunity.setWon(true);
        wonOpportunity.setClosedAt(LocalDate.now().minusDays(1));

        Opportunity lostOpportunity = new Opportunity(
                lead4,
                "Endereço Comercial - Carlos",
                Solution.COMMERCIAL_ADDRESS,
                new BigDecimal("1200.00"),
                commercialQualification,
                LocalDate.now().plusDays(10),
                "Oportunidade encerrada após comparação com outro fornecedor."
        );
        lostOpportunity.setWon(false);
        lostOpportunity.setClosedAt(LocalDate.now().minusDays(2));
        lostOpportunity.setLossReason("Cliente optou por fornecedor concorrente com menor preço.");

        opportunityRepository.saveAll(List.of(
                openOpportunity1,
                openOpportunity2,
                wonOpportunity,
                lostOpportunity
        ));

        Task task1 = new Task(
                userAdmin,
                "Entrar em contato",
                "Ligar no número " + openOpportunity1.getLead().getPhone(),
                TaskStatus.PENDING,
                LocalDate.now().plusDays(2),
                null,
                openOpportunity1
        );

        Task task2 = new Task(
                userAdmin,
                "Enviar contrato",
                "Mandar contrato por e-mail para " + openOpportunity2.getLead().getEmail(),
                TaskStatus.PENDING,
                LocalDate.now().plusDays(1),
                null,
                openOpportunity2
        );

        Task task3 = new Task(
                user1,
                "Qualificação de lead",
                "Realizar a primeira chamada para entender a dor do cliente: " + lead5.getName(),
                TaskStatus.PENDING,
                LocalDate.now().plusDays(3),
                lead5,
                null
        );

        Task task4 = new Task(
                user1,
                "Registrar perda da negociação",
                "Conferir se o motivo de perda da oportunidade foi registrado corretamente.",
                TaskStatus.COMPLETED,
                LocalDate.now().minusDays(1),
                null,
                lostOpportunity
        );

        Task task5 = new Task(
                user2,
                "Follow-up pós-venda",
                "Entrar em contato com " + wonOpportunity.getLead().getCompanyName() + " para validar onboarding.",
                TaskStatus.PENDING,
                LocalDate.now().plusDays(5),
                null,
                wonOpportunity
        );

        taskRepository.saveAll(List.of(task1, task2, task3, task4, task5));
    }

    private User createAdminUser() {
        User userAdmin = new User(
                "Albert Einstein",
                "einstein@gmail.com",
                encoder.encode("123456")
        );
        userAdmin.addRole(Role.ADMIN);
        return userAdmin;
    }

    private User createUser(String name, String email) {
        return new User(
                name,
                email,
                encoder.encode("123456")
        );
    }

    private Stage createStage(Pipeline pipeline, String name, String code, Integer position) {
        Stage stage = new Stage(name, code, position, pipeline);
        pipeline.addStage(stage);
        return stage;
    }

    private void clear() {
        taskRepository.deleteAll();
        taskRepository.flush();

        opportunityRepository.deleteAll();
        opportunityRepository.flush();

        stageRepository.deleteAll();
        stageRepository.flush();

        pipelineRepository.deleteAll();
        pipelineRepository.flush();

        leadRepository.deleteAll();
        leadRepository.flush();

        userRepository.deleteAll();
        userRepository.flush();
    }
}