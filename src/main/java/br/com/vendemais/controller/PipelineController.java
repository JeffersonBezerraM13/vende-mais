package br.com.vendemais.controller;

import br.com.vendemais.domain.dtos.pipeline.PipelineRequestDTO;
import br.com.vendemais.domain.dtos.pipeline.PipelineResponseDTO;
import br.com.vendemais.domain.dtos.stage.StageRequestDTO;
import br.com.vendemais.domain.dtos.stage.StageResponseDTO;
import br.com.vendemais.service.PipelineService;
import br.com.vendemais.service.StageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/pipelines")
public class PipelineController {

    private final PipelineService pipelineService;

    private final StageService stageService;

    public PipelineController(PipelineService pipelineService, StageService stageService) {
        this.pipelineService = pipelineService;
        this.stageService = stageService;
    }

    @GetMapping
    public ResponseEntity<Page<PipelineResponseDTO>> findAll(
            // Se o front-end não mandar nada, por padrão:
            // Traz a página 0, com 10 itens, ordenado pelo 'id' do mais novo pro mais velho
            @PageableDefault(page = 0,size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        Page<PipelineResponseDTO> page = pipelineService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PipelineResponseDTO> findById(@PathVariable Long id){
        PipelineResponseDTO pipelineResponseDTO = pipelineService.findById(id);
        return ResponseEntity.ok().body(pipelineResponseDTO);
    }

    @PostMapping("/create")
    public ResponseEntity<PipelineResponseDTO> create(@RequestBody @Valid PipelineRequestDTO pipelineRequestDTO){
        PipelineResponseDTO pipelineResponseDTO = pipelineService.create(pipelineRequestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(
                        pipelineResponseDTO.id()
                ).toUri();

        return ResponseEntity.created(uri).body(pipelineResponseDTO);
    }

    @PostMapping("/{pipelineId}/stages")
    public ResponseEntity<StageResponseDTO> createStage(@PathVariable Long pipelineId , @RequestBody @Valid StageRequestDTO stageRequestDTO){
        return ResponseEntity.ok().body(stageService.createStage(pipelineId,stageRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PipelineResponseDTO> update(@PathVariable Long id,@RequestBody @Valid PipelineRequestDTO pipelineRequestDTO) {
        return ResponseEntity.ok(pipelineService.update(id, pipelineRequestDTO));

    }
    @PutMapping("/{pipelineID}/stages/{stageId}")
    public ResponseEntity<StageResponseDTO> updateStage(@PathVariable Long pipelineID, @PathVariable Long stageId, @RequestBody @Valid StageRequestDTO stageRequestDTO){
        return ResponseEntity.ok().body(stageService.updateStage(pipelineID,stageId,stageRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        pipelineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
