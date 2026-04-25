package br.com.vendemais.repository.specification;

import br.com.vendemais.domain.dtos.task.TaskFilterDTO;
import br.com.vendemais.domain.entity.Task;
import br.com.vendemais.domain.enums.filter.TaskDeadlineFilter;
import br.com.vendemais.domain.enums.filter.TaskLinkTypeFilter;
import br.com.vendemais.domain.enums.TaskStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {

    private TaskSpecification() {
    }

    public static Specification<Task> withFilters(TaskFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.search() != null && !filter.search().isBlank()) {
                String search = "%" + filter.search().trim().toLowerCase() + "%";

                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), search),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), search)
                ));
            }

            if (filter.status() != null) {
                predicates.add(criteriaBuilder.equal(root.get("taskStatus"), filter.status()));
            }

            if (filter.deadline() != null) {
                predicates.add(buildDeadlinePredicate(filter.deadline(), root, criteriaBuilder));
            }

            if (filter.linkType() != null) {
                predicates.add(buildLinkTypePredicate(filter.linkType(), root, criteriaBuilder));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate buildDeadlinePredicate(
            TaskDeadlineFilter deadline,
            jakarta.persistence.criteria.Root<Task> root,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder
    ) {
        LocalDate today = LocalDate.now();

        return switch (deadline) {
            case OVERDUE -> criteriaBuilder.and(
                    criteriaBuilder.lessThan(root.get("dueDate"), today),
                    criteriaBuilder.notEqual(root.get("taskStatus"), TaskStatus.COMPLETED)
            );

            case DUE_SOON -> criteriaBuilder.and(
                    criteriaBuilder.between(root.get("dueDate"), today, today.plusDays(3)),
                    criteriaBuilder.notEqual(root.get("taskStatus"), TaskStatus.COMPLETED)
            );
        };
    }

    private static Predicate buildLinkTypePredicate(
            TaskLinkTypeFilter linkType,
            jakarta.persistence.criteria.Root<Task> root,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder
    ) {
        return switch (linkType) {
            case LEAD -> criteriaBuilder.isNotNull(root.get("lead"));
            case OPPORTUNITY -> criteriaBuilder.isNotNull(root.get("opportunity"));
        };
    }
}