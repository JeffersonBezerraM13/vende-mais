package br.com.vendemais.repository;


import br.com.vendemais.domain.entity.Stage;
import br.com.vendemais.domain.enums.StageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndPipelineId(String name, Long pipelineId);

    boolean existsByPipelineIdAndPosition(Long pipelineId, Integer position);

    boolean existsByPipelineIdAndPositionAndIdNot(Long pipelineId, Integer position, Long id);

    boolean existsByPipelineIdAndType(Long pipelineId, StageType type);

    boolean existsByPipelineIdAndTypeAndIdNot(Long pipelineId, StageType type, Long id);

    Optional<Stage> findByIdAndPipelineId(Long id, Long pipelineId);
}
