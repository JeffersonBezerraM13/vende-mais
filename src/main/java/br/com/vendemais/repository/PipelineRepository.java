package br.com.vendemais.repository;


import br.com.vendemais.domain.entity.Pipeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, Long> {

}
