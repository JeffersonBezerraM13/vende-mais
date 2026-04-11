package br.com.vendemais.repository;


import br.com.vendemais.domain.entity.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {

    boolean existsByPipelineIdAndPosition(Long pipelineId, Integer position);

    Optional<Stage> findByIdAndPipelineId(Long id, Long pipelineId);
}
