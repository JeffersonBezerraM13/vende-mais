package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.pipeline.PipelineRequestDTO;
import br.com.vendemais.domain.dtos.pipeline.PipelineResponseDTO;
import br.com.vendemais.domain.entity.Pipeline;
import br.com.vendemais.repository.PipelineRepository;
import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Manages sales pipeline definitions that determine how opportunities are
 * organized inside the CRM.
 */
@Service
public class PipelineService {

    private final PipelineRepository pipelineRepository;

    public PipelineService(PipelineRepository pipelineRepository) {
        this.pipelineRepository = pipelineRepository;
    };

    /**
     * Retrieves pipelines in pages so the CRM can browse funnel definitions
     * without loading all records at once.
     *
     * @param pageable pagination and sorting instructions for the query
     * @return a page containing pipeline projections mapped to response DTOs
     */
    public Page<PipelineResponseDTO> findAll(Pageable pageable) {
        Page<Pipeline> paginaDePipelines = pipelineRepository.findAll(pageable);

        return paginaDePipelines.map(PipelineResponseDTO::daEntidade);
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
     * @throws DataIntegrityViolationException if another pipeline already uses the same title
     */
    public PipelineResponseDTO create(PipelineRequestDTO dto) {
        if(existsbyTitle(dto.title())){
            throw new DataIntegrityViolationException("Pipeline já está cadastrado no sistema");
        }

        Pipeline pipeline = new Pipeline(
                dto.title()
        );

        return PipelineResponseDTO.daEntidade(pipelineRepository.save(pipeline));
    }

    /**
     * Updates the descriptive data of an existing pipeline.
     *
     * @param id identifier of the pipeline being updated
     * @param dto payload containing the revised pipeline data
     * @return the persisted pipeline mapped to the API response DTO
     * @throws ObjectNotFoundException if the pipeline does not exist
     */
    public PipelineResponseDTO update(Long id, PipelineRequestDTO dto) {
        Pipeline pipeline = findPipelineById(id);

        pipeline.setTitle(dto.title());


        return PipelineResponseDTO.daEntidade(pipelineRepository.save(pipeline));
    }

    /**
     * Deletes a pipeline when it is no longer needed by the CRM operating model.
     *
     * @param id identifier of the pipeline to delete
     * @throws ObjectNotFoundException if the pipeline does not exist
     * @throws DataIntegrityViolationException if the pipeline is still referenced by related records
     */
    public void delete(Long id){
        Pipeline pipeline = findPipelineById(id);
        try{
            pipelineRepository.delete(pipeline);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Você não pode apagar este funil pois ele está sendo utilizado em etapas ou oportunidades.");
        }
    }

    private Pipeline findPipelineById(Long id) {
        return pipelineRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Pipeline não encontrado. ID:" +id));
    }
    
    public boolean existsbyTitle(String title) {
        return pipelineRepository.existsByTitle(title);
    }
}
