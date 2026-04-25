package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.stage.StageRequestDTO;
import br.com.vendemais.domain.dtos.stage.StageResponseDTO;
import br.com.vendemais.domain.entity.Pipeline;
import br.com.vendemais.domain.entity.Stage;
import br.com.vendemais.repository.PipelineRepository;
import br.com.vendemais.repository.StageRepository;
import br.com.vendemais.service.exceptions.BusinessRuleException;
import br.com.vendemais.service.exceptions.DuplicateResourceException;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import br.com.vendemais.service.exceptions.ResourceInUseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages pipeline stages, including their technical code, display name and
 * position inside each sales funnel.
 */
@Service
@Transactional(readOnly = true)
public class StageService {

    private final StageRepository stageRepository;
    private final PipelineRepository pipelineRepository;

    public StageService(
            StageRepository stageRepository,
            PipelineRepository pipelineRepository
    ) {
        this.stageRepository = stageRepository;
        this.pipelineRepository = pipelineRepository;
    }

    /**
     * Retrieves stages in pages so administrators can inspect funnel steps.
     *
     * @param pageable pagination and sorting instructions for the query
     * @return a page containing stage projections mapped to response DTOs
     */
    public Page<StageResponseDTO> findAll(Pageable pageable) {
        return stageRepository.findAll(pageable)
                .map(StageResponseDTO::daEntidade);
    }

    /**
     * Loads a stage by identifier.
     *
     * @param id identifier of the stage to retrieve
     * @return the requested stage mapped to the API response DTO
     * @throws ObjectNotFoundException if the stage does not exist
     */
    public StageResponseDTO findById(Long id) {
        Stage stage = findStageById(id);
        return StageResponseDTO.daEntidade(stage);
    }

    /**
     * Creates a stage inside an existing pipeline after validating that its code
     * and position are unique within that pipeline.
     *
     * @param dto payload describing the stage to create
     * @return the persisted stage mapped to the API response DTO
     * @throws ObjectNotFoundException if the referenced pipeline does not exist
     * @throws DuplicateResourceException if the code or position already exists in the pipeline
     */
    @Transactional
    public StageResponseDTO createStage(StageRequestDTO dto) {
        Pipeline pipeline = findPipelineById(dto.pipelineId());

        String normalizedCode = normalizeCode(dto.code());
        Integer position = dto.position();

        ensureCodeAvailableForCreation(pipeline.getId(), normalizedCode);
        ensurePositionAvailableForCreation(pipeline.getId(), position);

        Stage stage = new Stage(
                normalizeName(dto.name()),
                normalizedCode,
                position,
                pipeline
        );

        pipeline.addStage(stage);

        return StageResponseDTO.daEntidade(stageRepository.save(stage));
    }

    /**
     * Updates the display data of a stage while keeping its technical code
     * immutable and ensuring the new position does not conflict inside the same
     * pipeline.
     *
     * @param stageId identifier of the stage being updated
     * @param dto payload containing the revised stage data
     * @return the updated stage mapped to the API response DTO
     * @throws ObjectNotFoundException if the pipeline or stage does not exist
     * @throws DuplicateResourceException if another stage already uses the new position
     * @throws BusinessRuleException if the request tries to change the stage code
     */
    @Transactional
    public StageResponseDTO updateStage(Long stageId, StageRequestDTO dto) {
        Pipeline pipeline = findPipelineById(dto.pipelineId());
        Stage stage = findStageByIdAndPipelineId(stageId, pipeline.getId());

        String normalizedCode = normalizeCode(dto.code());

        ensureCodeWasNotChanged(stage, normalizedCode);
        ensurePositionAvailableForUpdate(pipeline.getId(), stageId, dto.position());

        stage.setName(normalizeName(dto.name()));
        stage.setPosition(dto.position());

        return StageResponseDTO.daEntidade(stageRepository.save(stage));
    }

    /**
     * Deletes a stage from a pipeline when it is no longer part of the funnel
     * configuration.
     *
     * @param pipelineId identifier of the pipeline that owns the stage
     * @param stageId identifier of the stage to delete
     * @throws ObjectNotFoundException if the pipeline or stage does not exist
     * @throws ResourceInUseException if the stage is still referenced by opportunities
     */
    @Transactional
    public void deleteStage(Long pipelineId, Long stageId) {
        Pipeline pipeline = findPipelineById(pipelineId);
        Stage stage = findStageByIdAndPipelineId(stageId, pipeline.getId());

        try {
            stageRepository.delete(stage);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new ResourceInUseException(
                    "Você não pode apagar esta etapa pois ela está sendo utilizada em oportunidades.",
                    ex
            );
        }
    }

    private void ensureCodeAvailableForCreation(Long pipelineId, String code) {
        if (stageRepository.existsByPipelineIdAndCodeIgnoreCase(pipelineId, code)) {
            throw new DuplicateResourceException("Já existe uma etapa com este código neste funil.");
        }
    }

    private void ensurePositionAvailableForCreation(Long pipelineId, Integer position) {
        if (stageRepository.existsByPipelineIdAndPosition(pipelineId, position)) {
            throw new DuplicateResourceException("Já existe uma etapa com esta posição neste funil.");
        }
    }

    private void ensurePositionAvailableForUpdate(Long pipelineId, Long stageId, Integer position) {
        if (stageRepository.existsByPipelineIdAndPositionAndIdNot(pipelineId, position, stageId)) {
            throw new DuplicateResourceException("Já existe outra etapa com esta posição neste funil.");
        }
    }

    private void ensureCodeWasNotChanged(Stage stage, String requestedCode) {
        if (!stage.getCode().equalsIgnoreCase(requestedCode)) {
            throw new BusinessRuleException("O código técnico da etapa não pode ser alterado após a criação.");
        }
    }

    private String normalizeName(String name) {
        return name == null ? null : name.trim();
    }

    private String normalizeCode(String code) {
        return code == null ? null : code.trim().toUpperCase();
    }

    private Pipeline findPipelineById(Long id) {
        return pipelineRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Pipeline não encontrado. ID: " + id));
    }

    private Stage findStageById(Long id) {
        return stageRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Stage não encontrada. ID: " + id));
    }

    private Stage findStageByIdAndPipelineId(Long stageId, Long pipelineId) {
        return stageRepository.findByIdAndPipelineId(stageId, pipelineId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Stage não encontrada neste pipeline. Stage ID: " + stageId + ", Pipeline ID: " + pipelineId
                ));
    }
}