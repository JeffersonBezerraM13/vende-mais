package br.com.vendemais.controller;

import br.com.vendemais.domain.dtos.lead.LeadResponseDTO;
import br.com.vendemais.service.LeadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
}
