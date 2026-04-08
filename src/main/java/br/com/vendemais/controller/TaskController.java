package br.com.vendemais.controller;

import br.com.vendemais.domain.dtos.task.TaskRequestDTO;
import br.com.vendemais.domain.dtos.task.TaskResponseDTO;
import br.com.vendemais.service.TaskService;
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
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @GetMapping
    public ResponseEntity<Page<TaskResponseDTO>> findAll(
            // Se o front-end não mandar nada, por padrão:
            // Traz a página 0, com 10 itens, ordenado pelo 'createdAt' do mais novo pro mais velho
            @PageableDefault(page = 0,size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<TaskResponseDTO> page = taskService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> findById(@PathVariable Long id){
        TaskResponseDTO taskResponseDTO = taskService.findById(id);
        return ResponseEntity.ok().body(taskResponseDTO);
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(@RequestBody @Valid TaskRequestDTO dto){
        TaskResponseDTO taskResponseDTO = taskService.create(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(
                        taskResponseDTO.id()
                ).toUri();

        return ResponseEntity.created(uri).body(taskResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> update(@PathVariable Long id,@RequestBody @Valid TaskRequestDTO dto){
        return ResponseEntity.ok(taskService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
