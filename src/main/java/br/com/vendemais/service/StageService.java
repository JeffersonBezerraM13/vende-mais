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

@Service
public class StageService {

    private final StageRepository stageRepository;

    private final PipelineRepository pipelineRepository;

    public StageService(StageRepository stageRepository,PipelineRepository pipelineRepository) {
        this.stageRepository = stageRepository;
        this.pipelineRepository = pipelineRepository;
    };

    public Page<StageResponseDTO> findAll(Pageable pageable) {
        return stageRepository.findAll(pageable).map(StageResponseDTO::daEntidade);
    }

    public StageResponseDTO findById(Long id) {
        return StageResponseDTO.daEntidade(findStageById(id));
    }

    public StageResponseDTO createStage(Long pipelineId, StageRequestDTO dto) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new DataIntegrityViolationException("Funil não encontrado"));

        validateStageCreation(pipelineId, dto);

        Stage stage = new Stage(
                dto.name(),
                dto.code(),
                dto.position(),
                dto.type(),
                pipeline
        );

        pipeline.addStage(stage);

        return StageResponseDTO.daEntidade(stageRepository.save(stage));
    }

    public StageResponseDTO updateStage(Long pipelineId, Long stageId, StageRequestDTO dto) {
        Stage stage = stageRepository.findByIdAndPipelineId(stageId, pipelineId)
                .orElseThrow(() -> new ObjectNotFoundException("Esse estágio não pertence a esse funil"));

        validateStageUpdate(pipelineId, stageId, dto);

        stage.setName(dto.name());
        stage.setPosition(dto.position());
        stage.setType(dto.type());

        return StageResponseDTO.daEntidade(stageRepository.save(stage));
    }

    private void validateStageCreation(Long pipelineId, StageRequestDTO dto) {
        if (stageRepository.existsByPipelineIdAndPosition(pipelineId, dto.position())) {
            throw new DataIntegrityViolationException("Já existe uma etapa com essa posição neste funil");
        }

        validateUniqueTerminalType(pipelineId, dto.type(), null);
    }

    private void validateStageUpdate(Long pipelineId, Long stageId, StageRequestDTO dto) {
        if (stageRepository.existsByPipelineIdAndPositionAndIdNot(pipelineId, dto.position(), stageId)) {
            throw new DataIntegrityViolationException("Já existe uma etapa com essa posição neste funil");
        }

        validateUniqueTerminalType(pipelineId, dto.type(), stageId);
    }

    private void validateUniqueTerminalType(Long pipelineId, StageType type, Long stageId) {
        if (type == StageType.WON) {
            boolean existsWon = stageId == null
                    ? stageRepository.existsByPipelineIdAndType(pipelineId, StageType.WON)
                    : stageRepository.existsByPipelineIdAndTypeAndIdNot(pipelineId, StageType.WON, stageId);

            if (existsWon) {
                throw new DataIntegrityViolationException("O funil já possui uma etapa do tipo WON");
            }
        }

        if (type == StageType.LOST) {
            boolean existsLost = stageId == null
                    ? stageRepository.existsByPipelineIdAndType(pipelineId, StageType.LOST)
                    : stageRepository.existsByPipelineIdAndTypeAndIdNot(pipelineId, StageType.LOST, stageId);

            if (existsLost) {
                throw new DataIntegrityViolationException("O funil já possui uma etapa do tipo LOST");
            }
        }
    }

    private Stage findStageById(Long id) {
        return stageRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Stage não encontrado. ID: " + id));
    }
}
