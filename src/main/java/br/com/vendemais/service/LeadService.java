package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.lead.LeadResponseDTO;
import br.com.vendemais.domain.entity.Lead;
import br.com.vendemais.repository.LeadRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LeadService {

    private final LeadRepository leadRepository;

    private LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    };

    public Page<LeadResponseDTO> findAll(Pageable pageable) {
        Page<Lead> paginaDeLeads = leadRepository.findAll(pageable);

        return paginaDeLeads.map(LeadResponseDTO::daEntidade);
    }
}
