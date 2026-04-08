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
