package br.com.vendemais.service.db;

import br.com.vendemais.domain.entity.*;
import br.com.vendemais.domain.enums.*;
import br.com.vendemais.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public void populateDb(){
        clear();

        Lead lead1 = new  Lead(
                "Ana Grey",
                "83998765432",
                "ana@gmail.com",
                PersonType.INDIVIDUAL,
                null,
                Solution.SELF_STORAGE,
                LeadSource.WHATSAPP,
                EntryMethod.MANUAL,
                LeadStatus.NEW,
                "Sem observação"
        );

        Lead lead2 = new  Lead(
                "Bob Blue",
                "83912345678",
                "bob@gmail.com",
                PersonType.COMPANY,
                "Empresa do Bob",
                Solution.COWORKING,
                LeadSource.PHONE_CALL,
                EntryMethod.MANUAL,
                LeadStatus.NEW,
                "Sem observação"
        );

        leadRepository.saveAll(Arrays.asList(lead1,lead2));

        User userAdmin = new User(
                "Albert Einstein",
                "einstein@gmail.com",
                encoder.encode("1234")
        );
        userAdmin.addRole(Role.ADMIN);

        User user1 = new User(
                "Nikola Tesla",
                "tesla@gmail.com",
                encoder.encode("1234")
        );
        user1.addRole(Role.USER);

        userRepository.saveAll(Arrays.asList(userAdmin,user1));

        Pipeline pipeline1op1 = new Pipeline("Funíl Comercial");

        pipelineRepository.save(pipeline1op1);

        Stage stg1 = new Stage("Novo lead", "NOVO_LEAD", 1, false, pipeline1op1);
        Stage stg2 = new Stage("Contato inicial", "CONTATO_INICIAL", 2, false, pipeline1op1);
        Stage stg3 = new Stage("Qualificação", "QUALIFICACAO", 3, false, pipeline1op1);
        Stage stg4 = new Stage("Proposta enviada", "PROPOSTA_ENVIADA", 4, false, pipeline1op1);
        Stage stg5 = new Stage("Ganho", "GANHO", 5, true, pipeline1op1);

        stageRepository.saveAll(Arrays.asList(stg1,stg2,stg3,stg4,stg5));
        pipeline1op1.addStage(stg1);
        pipeline1op1.addStage(stg2);
        pipeline1op1.addStage(stg3);
        pipeline1op1.addStage(stg4);
        pipeline1op1.addStage(stg5);

        Opportunity op1lead1 = new Opportunity(
                lead1,
                lead1.getInterestSolution().name() +" - "+ lead1.getName(),
                Solution.SELF_STORAGE,
                new BigDecimal("1000.00"),
                pipeline1op1,
                null,
                LocalDate.now().plusDays(14),
                "Nenhuma",
                "Sem notas"
        );
        lead1.setLeadStatus(LeadStatus.CONVERTED);

        leadRepository.save(lead1);
        opportunityRepository.save(op1lead1);

        Task task1 = new Task(
                "Entrar em contato com cliente",
                "Via ligação, número "+ op1lead1.getLead().getPhone(),
                TaskStatus.PENDING,
                LocalDate.now().plusDays(2),
                null,
                op1lead1
        );

        taskRepository.save(task1);
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
