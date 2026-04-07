package br.com.vendemais.controller;

import br.com.vendemais.domain.dtos.pipeline.PipelineRequestDTO;
import br.com.vendemais.domain.dtos.pipeline.PipelineResponseDTO;
import br.com.vendemais.service.PipelineService;
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

    public PipelineController(PipelineService pipelineService) {
        this.pipelineService = pipelineService;
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
    public ResponseEntity<PipelineResponseDTO> create(@RequestBody PipelineRequestDTO pipelineRequestDTO){
        PipelineResponseDTO pipelineResponseDTO = pipelineService.create(pipelineRequestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(
                        pipelineResponseDTO.id()
                ).toUri();

        return ResponseEntity.created(uri).body(pipelineResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PipelineResponseDTO> update(@PathVariable Long id,@RequestBody PipelineRequestDTO pipelineRequestDTO){
        return ResponseEntity.ok(pipelineService.update(id, pipelineRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        pipelineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
