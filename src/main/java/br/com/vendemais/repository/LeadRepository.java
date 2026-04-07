package br.com.vendemais.repository;


import br.com.vendemais.domain.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    boolean existsById(Long id);

    boolean existsByEmail(String email);
}
