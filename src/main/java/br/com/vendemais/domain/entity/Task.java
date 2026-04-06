package br.com.vendemais.domain.entity;

import br.com.vendemais.domain.enums.LeadStatus;
import br.com.vendemais.domain.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Uma task deve ter um nome")
    private String title;

    private String description;

    private Status status;

    @NotBlank(message = "Data de vencimento não pode ser vazia")
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @OneToMany
    @JoinColumn(name = "opportunity_id")
    private Opportunity opportunity;

    private LocalDate createdAt;

    private LocalDate updatedAt;
}
