package br.com.vendemais.repository;


import br.com.vendemais.domain.entity.Stage;
import br.com.vendemais.domain.enums.StageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {

    Optional<Stage> findByIdAndPipelineId(Long id, Long pipelineId);
}
