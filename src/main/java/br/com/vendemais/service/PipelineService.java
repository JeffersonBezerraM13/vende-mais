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

@Service
public class PipelineService {

    private final PipelineRepository pipelineRepository;

    public PipelineService(PipelineRepository pipelineRepository) {
        this.pipelineRepository = pipelineRepository;
    };

    public Page<PipelineResponseDTO> findAll(Pageable pageable) {
        Page<Pipeline> paginaDePipelines = pipelineRepository.findAll(pageable);

        return paginaDePipelines.map(PipelineResponseDTO::daEntidade);
    }

    public PipelineResponseDTO findById(Long id) {
        Pipeline pipeline = findPipelineById(id);
        return PipelineResponseDTO.daEntidade(pipeline);
    }

    public PipelineResponseDTO create(PipelineRequestDTO dto) {
        if(existsbyTitle(dto.title())){
            throw new DataIntegrityViolationException("Pipeline já está cadastrado no sistema");
        }

        Pipeline pipeline = new Pipeline(
                dto.title()
        );

        return PipelineResponseDTO.daEntidade(pipelineRepository.save(pipeline));
    }

    public PipelineResponseDTO update(Long id, PipelineRequestDTO dto) {
        Pipeline pipeline = findPipelineById(id);

        pipeline.setTitle(dto.title());


        return PipelineResponseDTO.daEntidade(pipelineRepository.save(pipeline));
    }

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
