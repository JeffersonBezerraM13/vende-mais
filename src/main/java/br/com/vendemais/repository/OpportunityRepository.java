package br.com.vendemais.repository;

import br.com.vendemais.domain.entity.Opportunity;
import br.com.vendemais.domain.enums.StageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {

    @Query("""
        select case when count(o) > 0 then true else false end
        from Opportunity o
        where o.lead.id = :leadId
          and o.currentStage.type = :type
    """)
    boolean existsByLeadIdAndStageType(@Param("leadId") Long leadId, @Param("type") StageType type);


}