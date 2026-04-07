package br.com.vendemais.service;

import br.com.vendemais.domain.dtos.pipeline.PipelineResponseDTO;
import br.com.vendemais.domain.dtos.stage.StageRequestDTO;
import br.com.vendemais.domain.dtos.stage.StageResponseDTO;
import br.com.vendemais.domain.entity.Pipeline;
import br.com.vendemais.domain.entity.Stage;
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

    @Autowired
    private PipelineRepository pipelineRepository;

    public StageService(StageRepository stageRepository) {
        this.stageRepository = stageRepository;
    };

    public Page<StageResponseDTO> findAll(Pageable pageable) {
        Page<Stage> paginaDeStages = stageRepository.findAll(pageable);

        return paginaDeStages.map(StageResponseDTO::daEntidade);
    }

    public StageResponseDTO findById(Long id) {
        Stage stage = findStageById(id);
        return StageResponseDTO.daEntidade(stage);
    }

    public StageResponseDTO createStage(Long pipelineId, StageRequestDTO stageRequestDTO) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId).orElseThrow(() -> new DataIntegrityViolationException("Funil não encontrado"));

        if(stageRepository.existsByNameAndPipelineId(stageRequestDTO.name(), pipelineId)) {
            throw new DataIntegrityViolationException("Já existe uma etapa com esse nome neste funil");
        }

        Stage stage = new Stage(
                stageRequestDTO.name(),
                stageRequestDTO.code(),
                stageRequestDTO.position(),
                stageRequestDTO.finalStage(),
                pipeline
        );
        pipeline.addStage(stage);

        return StageResponseDTO.daEntidade(stageRepository.save(stage));
    }

    public StageResponseDTO updateStage(Long pipelineId, Long stageId, StageRequestDTO stageRequestDTO) {
        Stage stage = stageRepository.findById(stageId).orElseThrow(() -> new DataIntegrityViolationException("Etapa não encontrada"));

        if(!stage.getId().equals(pipelineId)){
            throw new ObjectNotFoundException("Esse estagio não pertece e esse funil");
        }

        stage.setName(stageRequestDTO.name());
        stage.setPosition(stageRequestDTO.position());
        stage.setFinalStage(stageRequestDTO.finalStage());

        return StageResponseDTO.daEntidade(stageRepository.save(stage));
    }

    private Stage findStageById(Long id) {
        return stageRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Stage não encontrado. ID:" +id));
    }
}
