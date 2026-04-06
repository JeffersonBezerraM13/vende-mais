package br.com.vendemais.service.db;

import br.com.vendemais.domain.entity.Lead;
import br.com.vendemais.domain.enums.LeadOrigin;
import br.com.vendemais.domain.enums.LegalEntities;
import br.com.vendemais.domain.enums.Registrarion;
import br.com.vendemais.domain.enums.SolutionInterest;
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
                LegalEntities.NATURAL_PERSON,
                null,
                SolutionInterest.SELF_STORAGE,
                LeadOrigin.WHATSAPP,
                Registrarion.MANUAL,
                "IN_PROGRESS",
                "Sem observação",
                LocalDate.of(2025,3,26),
                LocalDate.now()
        );

        leadRepository.saveAll(Arrays.asList(lead1));
    }

    private void clear() {
        leadRepository.deleteAll();
    }
}
