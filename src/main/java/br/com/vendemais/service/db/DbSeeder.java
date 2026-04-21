package br.com.vendemais.service.db;

import br.com.vendemais.domain.entity.*;
import br.com.vendemais.domain.enums.*;
import br.com.vendemais.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

@Service
public class DbSeeder {

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PipelineRepository pipelineRepository;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Transactional
    public void populateDb(){
        clear();

        // O construtor já adiciona ROLE_USER automaticamente
        User userAdmin = new User(
                "Albert Einstein",
                "einstein@gmail.com",
                encoder.encode("123456")
        );
        userAdmin.addRole(Role.ADMIN);

        User user1 = new User(
                "Nikola Tesla",
                "tesla@gmail.com",
                encoder.encode("123456")
        );

        User user2 = new User(
                "Marie Curie",
                "curie@gmail.com",
                encoder.encode("123456")
        );

        userRepository.saveAll(Arrays.asList(userAdmin, user1, user2));

        Lead lead1 = new Lead(
                "Ana Grey",
                "83998765432",
                "ana@gmail.com",
                PersonType.INDIVIDUAL,
                null,
                Solution.SELF_STORAGE,
                LeadSource.WHATSAPP,
                EntryMethod.MANUAL,
                null
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
                "O Bob ligou procurando opções de Coworking para a equipe comercial da Blue Corp. Eles operam 100% remoto hoje, mas precisam de um espaço fixo com acesso à sala de reunião para receber clientes da região presencialmente duas vezes por semana. Pediu orçamento para 4 posições."
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
                "Lead capturado via formulário do site. A Green Inc é uma startup de São Paulo e está abrindo um CNPJ filial na Paraíba. Precisam apenas do serviço de Endereço Fiscal e Comercial para registro na junta comercial e gestão de correspondências. Informou ter urgência na contratação."
        );

        leadRepository.saveAll(Arrays.asList(lead1, lead2, lead3));

        Pipeline pipeline1 = new Pipeline("Funil Comercial");
        pipelineRepository.save(pipeline1);

        Stage stg1 = new Stage("Novo lead", "NOVO_LEAD_1", 1, pipeline1);
        Stage stg2 = new Stage("Contato inicial", "CONTATO_INICIAL_1", 2, pipeline1);
        Stage stg3 = new Stage("Qualificação", "QUALIFICACAO_1", 3, pipeline1);
        Stage stg4 = new Stage("Proposta enviada", "PROPOSTA_ENVIADA_1", 4, pipeline1);

        stageRepository.saveAll(Arrays.asList(stg1, stg2, stg3, stg4));

        Pipeline pipelineCoworking = new Pipeline("Locação de Espaços (Coworking)");
        pipelineRepository.save(pipelineCoworking);

        Stage stgv1 = new Stage("Contato Inicial", "CONTATO_INICIAL_2", 1, pipelineCoworking);
        Stage stgv2 = new Stage("Visita Agendada (Tour)", "VISITA_AGENDADA_2", 2, pipelineCoworking);
        Stage stgv3 = new Stage("Visita Realizada", "VISITA_REALIZADA_2", 3, pipelineCoworking);
        Stage stgv4 = new Stage("Proposta Enviada", "PROPOSTA_ENVIADA_2", 4, pipelineCoworking);
        Stage stgv5 = new Stage("Revisão de Contrato", "REVISAO_CONTRATO_2", 5, pipelineCoworking);

        stageRepository.saveAll(Arrays.asList(stgv1, stgv2, stgv3, stgv4, stgv5));

        Opportunity op1 = new Opportunity(
                lead1,
                "Self Storage - Ana",
                Solution.SELF_STORAGE,
                new BigDecimal("1000.00"),
                stg1,
                LocalDate.now().plusDays(14),
                null
        );

        Opportunity op2 = new Opportunity(
                lead2,
                "Coworking - Bob",
                Solution.COWORKING,
                new BigDecimal("4500.00"),
                stg4,
                LocalDate.now().plusDays(15),
                null
        );

        opportunityRepository.saveAll(Arrays.asList(op1, op2));

        Task task1 = new Task(
                userAdmin,
                "Entrar em contato",
                "Ligar no número " + op1.getLead().getPhone(),
                TaskStatus.PENDING,
                LocalDate.now().plusDays(2),
                null,
                op1
        );

        Task task2 = new Task(
                userAdmin,
                "Enviar contrato",
                "Mandar por email para " + op2.getLead().getEmail(),
                TaskStatus.PENDING,
                LocalDate.now().plusDays(1),
                null,
                op2
        );

        Task task3 = new Task(
                user1,
                "Qualificação de Lead",
                "Realizar a primeira chamada para entender a dor do cliente: " + lead1.getName(),
                TaskStatus.PENDING,
                LocalDate.now().plusDays(3),
                lead1,
                null
        );

        Task task4 = new Task(
                user1,
                "Reunião de Demonstração",
                "Apresentar o dashboard do VendeMais para os tomadores de decisão da " + op2.getLead().getCompanyName(),
                TaskStatus.PENDING,
                LocalDate.now().plusDays(5),
                null,
                op2
        );

        taskRepository.saveAll(Arrays.asList(task1, task2, task3, task4));
    }

    private void clear() {
        leadRepository.deleteAll();
        userRepository.deleteAll();
        opportunityRepository.deleteAll();
        taskRepository.deleteAll();
        pipelineRepository.deleteAll();
        stageRepository.deleteAll();
    }
}
