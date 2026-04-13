package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.stage.StageRequestDTO;
import br.com.vendemais.domain.dtos.stage.StageResponseDTO;
import br.com.vendemais.domain.entity.Pipeline;
import br.com.vendemais.domain.entity.Stage;
import br.com.vendemais.repository.PipelineRepository;
import br.com.vendemais.repository.StageRepository;
import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles stage catalog operations inside pipelines so opportunity movement
 * follows a defined ordering.
 */
@Service
@Transactional(readOnly = true)
public class StageService {

    private final StageRepository stageRepository;
    private final PipelineRepository pipelineRepository;

    public StageService(StageRepository stageRepository,PipelineRepository pipelineRepository) {
        this.stageRepository = stageRepository;
        this.pipelineRepository = pipelineRepository;
    };

    /**
     * Retrieves stages in pages so clients can browse the configured funnel
     * checkpoints.
     *
     * @param pageable pagination and sorting instructions for the query
     * @return a page containing stage projections mapped to response DTOs
     */
    public Page<StageResponseDTO> findAll(Pageable pageable) {
        return stageRepository.findAll(pageable).map(StageResponseDTO::daEntidade);
    }

    /**
     * Loads a single stage so clients can inspect its ordering and owning
     * pipeline.
     *
     * @param id identifier of the stage to retrieve
     * @return the requested stage mapped to the API response DTO
     * @throws ObjectNotFoundException if the stage does not exist
     */
    public StageResponseDTO findById(Long id) {
        return StageResponseDTO.daEntidade(findStageById(id));
    }

    /**
     * Creates a stage inside an existing pipeline and appends it to the pipeline
     * stage collection.
     *
     * @param dto payload describing the stage to create
     * @return the persisted stage mapped to the API response DTO
     * @throws DataIntegrityViolationException if the referenced pipeline does not exist
     */
    @Transactional
    public StageResponseDTO createStage(@Valid StageRequestDTO dto) {
        try{
            if (stageRepository.existsByPipelineIdAndPosition(dto.pipelineId(), dto.position())) {
                throw new ObjectNotFoundException("Já existe uma etapa na posição " + dto.position() + " para este funil.");
            }

            Pipeline pipeline = pipelineRepository.findById(dto.pipelineId())
                    .orElseThrow(() -> new DataIntegrityViolationException("Funil não encontrado."));

            Stage stage = new Stage(
                    dto.name(),
                    dto.code(),
                    dto.position(),
                    pipeline
            );

            pipeline.addStage(stage);

            return StageResponseDTO.daEntidade(stageRepository.save(stage));
        } catch (org.springframework.dao.DataIntegrityViolationException e){
            throw new DataIntegrityViolationException("O código das etapas deve ser único globalmente.");
        }

    }

    /**
     * Updates a stage only when it belongs to the informed pipeline, preventing
     * cross-pipeline edits.
     *
     * @param stageId identifier of the stage being updated
     * @param dto payload containing the revised stage data
     * @return the persisted stage mapped to the API response DTO
     * @throws ObjectNotFoundException if the stage does not belong to the informed pipeline
     */
    @Transactional
    public StageResponseDTO updateStage(Long stageId,@Valid StageRequestDTO dto) {
        Stage stage = stageRepository.findByIdAndPipelineId(stageId, dto.pipelineId())
                .orElseThrow(() -> new ObjectNotFoundException("Esse estágio não pertence a esse funil"));

        stage.setName(dto.name());
        stage.setPosition(dto.position());

        return StageResponseDTO.daEntidade(stageRepository.save(stage));
    }

    /**
     * Deletes a specific stage, ensuring beforehand that it belongs to the informed pipeline.
     *
     * @param pipelineId the unique identifier of the pipeline.
     * @param stageId the unique identifier of the stage to be deleted.
     * @throws ObjectNotFoundException if the pipeline or the stage is not found in the database.
     * @throws DataIntegrityViolationException if the stage is found but does not belong to the provided pipeline.
     */
    @Transactional
    public void deleteStage(Long pipelineId, Long stageId) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new ObjectNotFoundException("Pipeline não encontrada"));

        Stage stage = findStageById(stageId);

        if(!pipeline.getId().equals(stage.getPipeline().getId())) {
            throw new DataIntegrityViolationException("Essa etapa não pertence a esse pipeline");
        }

        stageRepository.delete(stage);
    }

    /**
     * Helper method to retrieve a stage by its unique identifier.
     *
     * @param id the unique identifier of the stage.
     * @return the {@link Stage} entity corresponding to the provided ID.
     * @throws ObjectNotFoundException if no stage is found with the provided ID.
     */
    private Stage findStageById(Long id) {
        return stageRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Stage não encontrado. ID: " + id));
    }
}
