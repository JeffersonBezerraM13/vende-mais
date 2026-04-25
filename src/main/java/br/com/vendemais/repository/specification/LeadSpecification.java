package br.com.vendemais.repository.specification;

import br.com.vendemais.domain.dtos.lead.LeadFilterDTO;
import br.com.vendemais.domain.entity.Lead;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class LeadSpecification {

    private LeadSpecification() {
    }

    public static Specification<Lead> withFilters(LeadFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.search() != null && !filter.search().isBlank()) {
                String search = "%" + filter.search().trim().toLowerCase() + "%";

                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), search),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), search),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), search),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("companyName")), search)
                ));
            }

            if (filter.personType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("personType"), filter.personType()));
            }

            if (filter.leadSource() != null) {
                predicates.add(criteriaBuilder.equal(root.get("leadSource"), filter.leadSource()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}