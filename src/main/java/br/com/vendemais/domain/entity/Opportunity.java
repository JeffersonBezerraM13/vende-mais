package br.com.vendemais.domain.entity;

import br.com.vendemais.domain.enums.Solution;
import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a qualified sales opportunity tracked through a pipeline until it
 * is won or lost.
 */
@Entity
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Solution definitiveSolution;

    @Column(precision = 19, scale = 2)
    private BigDecimal estimatedValue;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "current_stage_id", nullable = false)
    private Stage currentStage;

    private boolean won;

    private LocalDate expectedCloseDate;

    private LocalDate closedAt;

    private String lossReason;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    public Opportunity(Lead lead, String title, Solution definitiveSolution, BigDecimal estimatedValue, Stage currentStage, LocalDate expectedCloseDate, String notes) {
        this.lead = lead;
        this.title = title;
        this.definitiveSolution = definitiveSolution;
        this.estimatedValue = estimatedValue;
        this.currentStage = currentStage;
        this.won = false;
        this.expectedCloseDate = expectedCloseDate;
        this.notes = notes;
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

    public Solution getDefinitiveSolution() {
        return definitiveSolution;
    }

    public void setDefinitiveSolution(Solution definitiveSolution) {
        this.definitiveSolution = definitiveSolution;
    }

    public BigDecimal getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(BigDecimal estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public boolean isWon(){
        return this.won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    public LocalDate getExpectedCloseDate() {
        return expectedCloseDate;
    }

    public void setExpectedCloseDate(LocalDate expectedCloseDate) {
        this.expectedCloseDate = expectedCloseDate;
    }

    public LocalDate getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDate closedAt) {
        this.closedAt = closedAt;
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

    @PrePersist
    public void prePersist() {
        createdAt = LocalDate.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDate.now();
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
        if (this == o) {
            return true;
        }

        if (!(o instanceof Opportunity opportunity)) {
            return false;
        }

        if (id == null || opportunity.id == null) {
            return false;
        }

        return Objects.equals(id, opportunity.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
