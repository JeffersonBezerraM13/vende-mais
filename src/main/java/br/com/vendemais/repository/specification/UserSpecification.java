package br.com.vendemais.repository.specification;

import br.com.vendemais.domain.dtos.user.UserFilterDTO;
import br.com.vendemais.domain.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    private UserSpecification() {
    }

    public static Specification<User> withFilters(UserFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.search() != null && !filter.search().isBlank()) {
                String search = "%" + filter.search().trim().toLowerCase() + "%";

                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), search),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), search)
                ));
            }

            if (filter.role() != null) {
                predicates.add(criteriaBuilder.isMember(filter.role().getCode(), root.get("roles")));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}