package br.com.vendemais.controller;

import br.com.vendemais.domain.dtos.stage.StageRequestDTO;
import br.com.vendemais.domain.dtos.stage.StageResponseDTO;
import br.com.vendemais.domain.entity.Pipeline;
import br.com.vendemais.repository.PipelineRepository;
import br.com.vendemais.service.StageService;
import br.com.vendemais.service.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/stages")
public class StageController {

    private final StageService stageService;

    public StageController(StageService stageService) {
        this.stageService = stageService;
    }

    @GetMapping
    public ResponseEntity<Page<StageResponseDTO>> findAll(
            // Se o front-end não mandar nada, por padrão:
            // Traz a página 0, com 10 itens, ordenado pelo 'id' do mais novo pro mais velho
            @PageableDefault(page = 0,size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        Page<StageResponseDTO> page = stageService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StageResponseDTO> findById(@PathVariable Long id) {
        StageResponseDTO stageResponseDTO = stageService.findById(id);
        return ResponseEntity.ok().body(stageResponseDTO);
    }
}
