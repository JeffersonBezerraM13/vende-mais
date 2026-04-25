package br.com.vendemais.repository.specification;

import br.com.vendemais.domain.dtos.opportunity.OpportunityFilterDTO;
import br.com.vendemais.domain.entity.Opportunity;
import br.com.vendemais.domain.entity.Stage;
import br.com.vendemais.domain.enums.OpportunityStatusFilter;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OpportunitySpecification {

    private OpportunitySpecification() {
    }

    public static Specification<Opportunity> withFilters(OpportunityFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Object, Object> leadJoin = root.join("lead");
            Join<Opportunity, Stage> stageJoin = root.join("currentStage");
            Join<Object, Object> pipelineJoin = stageJoin.join("pipeline");

            if (filter.search() != null && !filter.search().isBlank()) {
                String search = "%" + filter.search().trim().toLowerCase() + "%";

                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(leadJoin.get("name")), search),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), search),
                        criteriaBuilder.like(criteriaBuilder.lower(pipelineJoin.get("title")), search),
                        criteriaBuilder.like(criteriaBuilder.lower(stageJoin.get("name")), search),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("notes")), search)
                ));
            }

            if (filter.pipelineId() != null) {
                predicates.add(criteriaBuilder.equal(pipelineJoin.get("id"), filter.pipelineId()));
            }

            if (filter.status() != null) {
                predicates.add(buildStatusPredicate(filter.status(), root, criteriaBuilder));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate buildStatusPredicate(
            OpportunityStatusFilter status,
            jakarta.persistence.criteria.Root<Opportunity> root,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder
    ) {
        return switch (status) {
            case OPEN -> criteriaBuilder.and(
                    criteriaBuilder.isFalse(root.get("won")),
                    criteriaBuilder.isNull(root.get("closedAt"))
            );
            case WON -> criteriaBuilder.isTrue(root.get("won"));
            case LOST -> criteriaBuilder.and(
                    criteriaBuilder.isFalse(root.get("won")),
                    criteriaBuilder.isNotNull(root.get("closedAt"))
            );
        };
    }
}