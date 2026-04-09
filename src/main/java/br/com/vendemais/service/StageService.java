package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.pipeline.PipelineResponseDTO;
import br.com.vendemais.domain.dtos.stage.StageRequestDTO;
import br.com.vendemais.domain.dtos.stage.StageResponseDTO;
import br.com.vendemais.domain.entity.Pipeline;
import br.com.vendemais.domain.entity.Stage;
import br.com.vendemais.domain.enums.StageType;
import br.com.vendemais.repository.PipelineRepository;
import br.com.vendemais.repository.StageRepository;
import br.com.vendemais.service.exceptions.DataIntegrityViolationException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Handles stage catalog operations inside pipelines so opportunity movement
 * follows a defined ordering.
 */
@Service
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
    public StageResponseDTO createStage(StageRequestDTO dto) {
        Pipeline pipeline = pipelineRepository.findById(dto.pipelineId())
                .orElseThrow(() -> new DataIntegrityViolationException("Funil não encontrado"));

        Stage stage = new Stage(
                dto.name(),
                dto.code(),
                dto.position(),
                pipeline
        );

        pipeline.addStage(stage);

        return StageResponseDTO.daEntidade(stageRepository.save(stage));
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
    public StageResponseDTO updateStage(Long stageId, StageRequestDTO dto) {
        Stage stage = stageRepository.findByIdAndPipelineId(stageId, dto.pipelineId())
                .orElseThrow(() -> new ObjectNotFoundException("Esse estágio não pertence a esse funil"));

        stage.setName(dto.name());
        stage.setPosition(dto.position());

        return StageResponseDTO.daEntidade(stageRepository.save(stage));
    }

    private Stage findStageById(Long id) {
        return stageRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Stage não encontrado. ID: " + id));
    }
}
