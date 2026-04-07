package br.com.vendemais.repository;

import br.com.vendemais.domain.entity.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {

    // O Spring entende: "Existe alguma Oportunidade onde o ID do Lead seja X e o 'finalStage' da Etapa Atual seja falso?"
    boolean existsByLeadIdAndCurrentStage_FinalStageFalse(Long leadId);
}