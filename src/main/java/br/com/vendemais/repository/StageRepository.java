package br.com.vendemais.repository;


import br.com.vendemais.domain.entity.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndPipelineId(String name, Long pipelineId);
}
