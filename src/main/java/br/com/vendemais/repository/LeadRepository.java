package br.com.vendemais.repository;

import br.com.vendemais.domain.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long>, JpaSpecificationExecutor<Lead> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}