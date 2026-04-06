package br.com.vendemais.service.db;

import br.com.vendemais.domain.entity.Lead;
import br.com.vendemais.domain.enums.*;
import br.com.vendemais.repository.LeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;

@Service
public class DbSeeder {

    @Autowired
    private LeadRepository leadRepository;

    public void populateDb(){
        clear();

        Lead lead1 = new  Lead(
                "Ana Grey",
                "83998765432",
                "ana@gmail.com",
                PersonType.INDIVIDUAL,
                null,
                InterestSolution.SELF_STORAGE,
                LeadSource.WHATSAPP,
                EntryMethod.MANUAL,
                LeadStatus.NEW,
                "Sem observação"
        );

        leadRepository.saveAll(Arrays.asList(lead1));
    }

    private void clear() {
        leadRepository.deleteAll();
    }
}
