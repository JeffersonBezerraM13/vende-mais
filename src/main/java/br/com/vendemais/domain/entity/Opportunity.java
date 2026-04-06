package br.com.vendemais.domain.entity;

import br.com.vendemais.domain.enums.InterestSolution;
import jakarta.persistence.*;


import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "lead_id")
    private Lead lead;

    private String title;

    private InterestSolution definitiveSolution ;

    private Float estimatedValue;

    @OneToOne
    private Pipeline pipeline;

    private LocalDate expectedCloseDate;

    private String lossReason;

    private String notes;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    public Opportunity(Lead lead, String title, InterestSolution definitiveSolution, Float estimatedValue, Pipeline pipeline, LocalDate expectedCloseDate, String lossReason, String notes) {
        this.lead = lead;
        this.title = title;
        this.definitiveSolution = definitiveSolution;
        this.estimatedValue = estimatedValue;
        this.pipeline = pipeline;
        this.expectedCloseDate = expectedCloseDate;
        this.lossReason = lossReason;
        this.notes = notes;
        this.createdAt = LocalDate.now();
    }

    protected Opportunity() {
    }

    public Long getId() {
        return id;
    }

    public Lead getLead() {
        return lead;
    }

    public void setLead(Lead lead) {
        this.lead = lead;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public InterestSolution getDefinitiveSolution() {
        return definitiveSolution;
    }

    public void setDefinitiveSolution(InterestSolution definitiveSolution) {
        this.definitiveSolution = definitiveSolution;
    }

    public Float getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(Float estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public LocalDate getExpectedCloseDate() {
        return expectedCloseDate;
    }

    public void setExpectedCloseDate(LocalDate expectedCloseDate) {
        this.expectedCloseDate = expectedCloseDate;
    }

    public String getLossReason() {
        return lossReason;
    }

    public void setLossReason(String lossReason) {
        this.lossReason = lossReason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Opportunity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
