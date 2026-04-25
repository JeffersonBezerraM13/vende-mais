package br.com.vendemais.repository;

import br.com.vendemais.domain.entity.Pipeline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, Long> {

    boolean existsByTitleIgnoreCase(String title);

    boolean existsByTitleIgnoreCaseAndIdNot(String title, Long id);

    Page<Pipeline> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}