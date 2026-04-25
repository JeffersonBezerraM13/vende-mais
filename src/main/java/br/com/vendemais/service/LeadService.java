package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.lead.LeadFilterDTO;
import br.com.vendemais.domain.dtos.lead.LeadRequestDTO;
import br.com.vendemais.domain.dtos.lead.LeadResponseDTO;
import br.com.vendemais.domain.entity.Lead;
import br.com.vendemais.repository.LeadRepository;
import br.com.vendemais.repository.specification.LeadSpecification;
import br.com.vendemais.service.exceptions.DuplicateResourceException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import br.com.vendemais.service.exceptions.ResourceInUseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Coordinates lead persistence, filtering, uniqueness validation, and lifecycle
 * rules for prospects entering the CRM.
 */
@Service
@Transactional(readOnly = true)
public class LeadService {

    private final LeadRepository leadRepository;

    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    /**
     * Retrieves leads in pages, applying optional filters so prospect lists can
     * be searched and narrowed directly by the backend instead of being filtered
     * in memory by the client application.
     *
     * @param filter optional filtering criteria, such as search term, person type
     *               and lead source
     * @param pageable pagination and sorting instructions for the query
     * @return a page containing filtered lead projections mapped to response DTOs
     */
    public Page<LeadResponseDTO> findAll(LeadFilterDTO filter, Pageable pageable) {
        Page<Lead> leadsPage = leadRepository.findAll(
                LeadSpecification.withFilters(filter),
                pageable
        );

        return leadsPage.map(LeadResponseDTO::daEntidade);
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
     * @throws DuplicateResourceException if another lead already uses the same email
     */
    @Transactional
    public LeadResponseDTO create(LeadRequestDTO dto) {
        String normalizedEmail = normalizeEmail(dto.email());

        ensureEmailAvailableForCreation(normalizedEmail);

        Lead lead = buildLead(dto, normalizedEmail);

        return LeadResponseDTO.daEntidade(leadRepository.save(lead));
    }

    /**
     * Updates an existing lead after validating that the new email does not
     * belong to another prospect.
     *
     * @param id identifier of the lead being updated
     * @param dto payload containing the revised lead data
     * @return the persisted lead mapped to the API response DTO
     * @throws ObjectNotFoundException if the lead does not exist
     * @throws DuplicateResourceException if the new email belongs to another lead
     */
    @Transactional
    public LeadResponseDTO update(Long id, LeadRequestDTO dto) {
        Lead lead = findLeadById(id);
        String normalizedEmail = normalizeEmail(dto.email());

        ensureEmailAvailableForUpdate(id, normalizedEmail);
        updateLeadData(lead, dto, normalizedEmail);

        return LeadResponseDTO.daEntidade(leadRepository.save(lead));
    }

    /**
     * Removes a lead from the CRM when it should no longer remain in the prospect
     * base.
     *
     * @param id identifier of the lead to delete
     * @throws ObjectNotFoundException if the lead does not exist
     * @throws ResourceInUseException if the lead is still referenced by related records
     */
    @Transactional
    public void delete(Long id) {
        Lead lead = findLeadById(id);

        try {
            leadRepository.delete(lead);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new ResourceInUseException(
                    "Você não pode apagar este lead pois ele está vinculado a tarefas ou oportunidades.",
                    ex
            );
        }
    }

    private Lead buildLead(LeadRequestDTO dto, String normalizedEmail) {
        return new Lead(
                dto.name(),
                dto.phone(),
                normalizedEmail,
                dto.personType(),
                dto.companyName(),
                dto.interestSoluction(),
                dto.leadSource(),
                dto.entryMethod(),
                dto.notes()
        );
    }

    private void updateLeadData(Lead lead, LeadRequestDTO dto, String normalizedEmail) {
        lead.setName(dto.name());
        lead.setPhone(dto.phone());
        lead.setEmail(normalizedEmail);
        lead.setPersonType(dto.personType());
        lead.setCompanyName(dto.companyName());
        lead.setInterestSoluction(dto.interestSoluction());
        lead.setLeadSource(dto.leadSource());
        lead.setEntryMethod(dto.entryMethod());
        lead.setNotes(dto.notes());
        lead.setUpdatedAt(LocalDate.now());
    }

    private void ensureEmailAvailableForCreation(String email) {
        if (leadRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Já existe um lead cadastrado com este e-mail.");
        }
    }

    private void ensureEmailAvailableForUpdate(Long leadId, String email) {
        if (leadRepository.existsByEmailAndIdNot(email, leadId)) {
            throw new DuplicateResourceException("Já existe outro lead cadastrado com este e-mail.");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private Lead findLeadById(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Lead não encontrado. ID: " + id));
    }
}