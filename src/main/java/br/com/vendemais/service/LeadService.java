package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.lead.LeadFilterDTO;
import br.com.vendemais.domain.dtos.lead.LeadRequestDTO;
import br.com.vendemais.domain.dtos.lead.LeadResponseDTO;
import br.com.vendemais.domain.entity.Lead;
import br.com.vendemais.repository.LeadRepository;

import br.com.vendemais.repository.specification.LeadSpecification;
import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Coordinates lead persistence and validation rules for prospects entering the
 * CRM.
 */
@Service
@Transactional(readOnly = true)
public class LeadService {

    private final LeadRepository leadRepository;

    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    };

    /**
     * Retrieves leads in pages, applying optional filters so prospect lists can be
     * searched and narrowed directly by the backend instead of being filtered in
     * memory by the client application.
     *
     * @param filter optional filtering criteria, such as search term, person type
     *               and lead source
     * @param pageable pagination and sorting instructions for the query
     * @return a page containing filtered lead projections mapped to response DTOs
     */
    public Page<LeadResponseDTO> findAll(LeadFilterDTO filter, Pageable pageable) {
        Page<Lead> paginaDeLeads = leadRepository.findAll(
                LeadSpecification.withFilters(filter),
                pageable
        );

        return paginaDeLeads.map(LeadResponseDTO::daEntidade);
    }

    /**
     * Loads a lead by identifier so client applications can inspect the full
     * prospect context.
     *
     * @param id identifier of the lead to retrieve
     * @return the requested lead mapped to the API response DTO
     * @throws ObjectNotFoundException if the lead does not exist
     */
    public LeadResponseDTO findById(Long id) {
        Lead lead = findLeadById(id);
        return LeadResponseDTO.daEntidade(lead);
    }

    /**
     * Registers a new lead after enforcing the CRM rule that each prospect email
     * must be unique.
     *
     * @param dto payload describing the lead being captured
     * @return the persisted lead mapped to the API response DTO
     * @throws DataIntegrityViolationException if another lead already uses the same email
     */
    @Transactional
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

    /**
     * Updates an existing lead so qualification data stays current across the CRM
     * workflow.
     *
     * @param id identifier of the lead being updated
     * @param dto payload containing the revised lead data
     * @return the persisted lead mapped to the API response DTO
     * @throws ObjectNotFoundException if the lead does not exist
     */
    @Transactional
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

    /**
     * Removes a lead from the CRM when it should no longer remain in the prospect
     * base.
     *
     * @param id identifier of the lead to delete
     * @throws ObjectNotFoundException if the lead does not exist
     */
    @Transactional
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
