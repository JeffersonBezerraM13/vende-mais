package br.com.vendemais.controller;

import br.com.vendemais.domain.dtos.lead.LeadRequestDTO;
import br.com.vendemais.domain.dtos.lead.LeadResponseDTO;
import br.com.vendemais.service.LeadService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping
    public ResponseEntity<Page<LeadResponseDTO>> findAll(
            // Se o front-end não mandar nada, por padrão:
            // Traz a página 0, com 10 itens, ordenado pelo 'createdAt' do mais novo pro mais velho
            @PageableDefault(page = 0,size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<LeadResponseDTO> page = leadService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeadResponseDTO> findById(@PathVariable Long id){
        LeadResponseDTO leadResponseDTO = leadService.findById(id);
        return ResponseEntity.ok().body(leadResponseDTO);
    }

    @PostMapping
    public ResponseEntity<LeadResponseDTO> create(@RequestBody @Valid LeadRequestDTO leadRequestDTO){
        LeadResponseDTO leadResponseDTO = leadService.create(leadRequestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(
                        leadResponseDTO.id()
                ).toUri();

        return ResponseEntity.created(uri).body(leadResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeadResponseDTO> update(@PathVariable Long id,@RequestBody @Valid LeadRequestDTO leadRequestDTO){
        return ResponseEntity.ok(leadService.update(id, leadRequestDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        leadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
