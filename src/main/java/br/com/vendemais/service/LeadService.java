package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.lead.LeadRequestDTO;
import br.com.vendemais.domain.dtos.lead.LeadResponseDTO;
import br.com.vendemais.domain.entity.Lead;
import br.com.vendemais.repository.LeadRepository;

import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LeadService {

    private final LeadRepository leadRepository;

    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    };

    public Page<LeadResponseDTO> findAll(Pageable pageable) {
        Page<Lead> paginaDeLeads = leadRepository.findAll(pageable);

        return paginaDeLeads.map(LeadResponseDTO::daEntidade);
    }

    public LeadResponseDTO findById(Long id) {
        Lead lead = findLeadById(id);
        return LeadResponseDTO.daEntidade(lead);
    }

    public LeadResponseDTO create(LeadRequestDTO dto) {
        if(existsByEmail(dto.email())){
            throw new DataIntegrityViolationException("Lead já está cadastrado no sistema");
        }

        Lead lead = new Lead(
                dto.name(),
                dto.phone(),
                dto.email(),
                dto.personType(),
                dto.companyName(),
                dto.interestSoluction(),
                dto.leadSource(),
                dto.entryMethod(),
                dto.notes()
        );

        return LeadResponseDTO.daEntidade(leadRepository.save(lead));
    }

    public LeadResponseDTO update(Long id, LeadRequestDTO dto) {
        Lead lead = findLeadById(id);

        lead.setName(dto.name());
        lead.setPhone(dto.phone());
        lead.setEmail(dto.email());
        lead.setPersonType(dto.personType());
        lead.setCompanyName(dto.companyName());
        lead.setInterestSoluction(dto.interestSoluction());
        lead.setLeadSource(dto.leadSource());
        lead.setEntryMethod(dto.entryMethod());
        lead.setNotes(dto.notes());
        lead.setUpdatedAt(LocalDate.now());

        return LeadResponseDTO.daEntidade(leadRepository.save(lead));
    }

    public void delete(Long id){
        Lead lead = findLeadById(id);
        leadRepository.delete(lead);
    }

    private Lead findLeadById(Long id) {
        return leadRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Lead não encontrado. ID:" +id));
    }

    private boolean existsByEmail(String email) {
        return leadRepository.existsByEmail(email);
    }
}
