package br.com.vendemais.controller;

import br.com.vendemais.domain.dtos.opportunity.OpportunityRequestDTO;
import br.com.vendemais.domain.dtos.opportunity.OpportunityResponseDTO;
import br.com.vendemais.service.OpportunityService;
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
@RequestMapping("/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    @GetMapping
    public ResponseEntity<Page<OpportunityResponseDTO>> findAll(
            // Se o front-end não mandar nada, por padrão:
            // Traz a página 0, com 10 itens, ordenado pelo 'createdAt' do mais novo pro mais velho
            @PageableDefault(page = 0,size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<OpportunityResponseDTO> page = opportunityService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpportunityResponseDTO> findById(@PathVariable Long id){
        OpportunityResponseDTO opportunityResponseDTO = opportunityService.findById(id);
        return ResponseEntity.ok().body(opportunityResponseDTO);
    }

    @GetMapping("/check-open")
    public ResponseEntity<Boolean> hasOpenOpportunities(@RequestParam Long leadId) {
        boolean hasOpen = opportunityService.hasOpenOpportunities(leadId);
        return ResponseEntity.ok(hasOpen);
    }

    @PostMapping
    public ResponseEntity<OpportunityResponseDTO> create(@RequestBody @Valid OpportunityRequestDTO opportunityRequestDTO){
        OpportunityResponseDTO opportunityResponseDTO = opportunityService.create(opportunityRequestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(
                        opportunityResponseDTO.id()
                ).toUri();

        return ResponseEntity.created(uri).body(opportunityResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OpportunityResponseDTO> update(@PathVariable Long id,@RequestBody @Valid OpportunityRequestDTO opportunityRequestDTO){
        return ResponseEntity.ok(opportunityService.update(id, opportunityRequestDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        opportunityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
