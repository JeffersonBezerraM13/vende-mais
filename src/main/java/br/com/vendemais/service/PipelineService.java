package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.pipeline.PipelineFilterDTO;
import br.com.vendemais.domain.dtos.pipeline.PipelineRequestDTO;
import br.com.vendemais.domain.dtos.pipeline.PipelineResponseDTO;
import br.com.vendemais.domain.entity.Pipeline;
import br.com.vendemais.repository.PipelineRepository;
import br.com.vendemais.service.exceptions.DuplicateResourceException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import br.com.vendemais.service.exceptions.ResourceInUseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages sales pipeline definitions that determine how opportunities are
 * organized inside the CRM.
 */
@Service
@Transactional(readOnly = true)
public class PipelineService {

    private final PipelineRepository pipelineRepository;

    public PipelineService(PipelineRepository pipelineRepository) {
        this.pipelineRepository = pipelineRepository;
    }

    /**
     * Retrieves pipelines in pages, applying an optional title search so CRM clients
     * can browse funnel definitions without loading all records at once.
     *
     * @param filter optional filtering criteria, such as the pipeline title search term
     * @param pageable pagination and sorting instructions for the query
     * @return a page containing filtered pipeline projections mapped to response DTOs
     */
    public Page<PipelineResponseDTO> findAll(PipelineFilterDTO filter, Pageable pageable) {
        Page<Pipeline> pipelinesPage = hasSearchTerm(filter)
                ? pipelineRepository.findByTitleContainingIgnoreCase(filter.search().trim(), pageable)
                : pipelineRepository.findAll(pageable);

        return pipelinesPage.map(PipelineResponseDTO::daEntidade);
    }

    /**
     * Loads a single pipeline together with its configured structure.
     *
     * @param id identifier of the pipeline to retrieve
     * @return the requested pipeline mapped to the API response DTO
     * @throws ObjectNotFoundException if the pipeline does not exist
     */
    public PipelineResponseDTO findById(Long id) {
        Pipeline pipeline = findPipelineById(id);
        return PipelineResponseDTO.daEntidade(pipeline);
    }

    /**
     * Creates a new pipeline after enforcing title uniqueness across CRM funnel
     * definitions.
     *
     * @param dto payload describing the pipeline being created
     * @return the persisted pipeline mapped to the API response DTO
     * @throws DuplicateResourceException if another pipeline already uses the same title
     */
    @Transactional
    public PipelineResponseDTO create(PipelineRequestDTO dto) {
        String normalizedTitle = normalizeTitle(dto.title());

        ensureTitleAvailableForCreation(normalizedTitle);

        Pipeline pipeline = new Pipeline(normalizedTitle);

        return PipelineResponseDTO.daEntidade(pipelineRepository.save(pipeline));
    }

    /**
     * Updates the descriptive data of an existing pipeline after validating that
     * the new title does not belong to another pipeline.
     *
     * @param id identifier of the pipeline being updated
     * @param dto payload containing the revised pipeline data
     * @return the persisted pipeline mapped to the API response DTO
     * @throws ObjectNotFoundException if the pipeline does not exist
     * @throws DuplicateResourceException if the new title belongs to another pipeline
     */
    @Transactional
    public PipelineResponseDTO update(Long id, PipelineRequestDTO dto) {
        Pipeline pipeline = findPipelineById(id);
        String normalizedTitle = normalizeTitle(dto.title());

        ensureTitleAvailableForUpdate(id, normalizedTitle);

        pipeline.setTitle(normalizedTitle);

        return PipelineResponseDTO.daEntidade(pipelineRepository.save(pipeline));
    }

    /**
     * Deletes a pipeline when it is no longer needed by the CRM operating model.
     *
     * @param id identifier of the pipeline to delete
     * @throws ObjectNotFoundException if the pipeline does not exist
     * @throws ResourceInUseException if the pipeline is still referenced by stages or opportunities
     */
    @Transactional
    public void delete(Long id) {
        Pipeline pipeline = findPipelineById(id);

        try {
            pipelineRepository.delete(pipeline);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new ResourceInUseException(
                    "Você não pode apagar este funil pois ele está sendo utilizado em etapas ou oportunidades.",
                    ex
            );
        }
    }

    private boolean hasSearchTerm(PipelineFilterDTO filter) {
        return filter != null && filter.search() != null && !filter.search().isBlank();
    }

    private String normalizeTitle(String title) {
        return title == null ? null : title.trim();
    }

    private void ensureTitleAvailableForCreation(String title) {
        if (pipelineRepository.existsByTitleIgnoreCase(title)) {
            throw new DuplicateResourceException("Já existe um funil cadastrado com este título.");
        }
    }

    private void ensureTitleAvailableForUpdate(Long pipelineId, String title) {
        if (pipelineRepository.existsByTitleIgnoreCaseAndIdNot(title, pipelineId)) {
            throw new DuplicateResourceException("Já existe outro funil cadastrado com este título.");
        }
    }

    private Pipeline findPipelineById(Long id) {
        return pipelineRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Pipeline não encontrado. ID: " + id));
    }
}