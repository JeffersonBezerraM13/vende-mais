package br.com.vendemais.repository;


import br.com.vendemais.domain.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    void deleteByOpportunityId(Long opportunityId);
}
