package br.com.vendemais.repository;

import br.com.vendemais.domain.entity.Opportunity;
import br.com.vendemais.domain.enums.OpportunityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {

    boolean existsByLeadIdAndStatus(Long leadId, OpportunityStatus status);
}