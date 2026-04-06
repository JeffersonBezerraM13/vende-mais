package br.com.vendemais.domain.entity;

import br.com.vendemais.domain.enums.LeadOrigin;
import br.com.vendemais.domain.enums.LegalEntities;
import br.com.vendemais.domain.enums.Registrarion;
import br.com.vendemais.domain.enums.SolutionInterest;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome não pode ser vazio")
    private String name;

    @NotBlank(message = "Telefone não pode ser vazio")
    private String phoneNumber;

    @NotBlank(message = "Email não pode ser vazio")
    @Email
    private String email;

    private LegalEntities legalEntities;

    private String EnterpriseName;

    private SolutionInterest soluctionInterest;

    @NotNull(message = "Origem do lead não pode ser vazia")
    private LeadOrigin leadOrigin;

    @NotNull (message = "Forma de registor do lead não pode ser vazia")
    private Registrarion registration;

    //lead com status desqualificado não pode ser convertido em oportunidade
    //lead convertido continua existindo, mas deve ficar marcado como convertido
    private String leadStatus;

    private String observations;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    public Lead(String name, String phoneNumber, String email, LegalEntities legalEntities, String enterpriseName, SolutionInterest soluctionInterest, LeadOrigin leadOrigin, Registrarion registration, String leadStatus, String observations, LocalDate createdAt, LocalDate updatedAt) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.legalEntities = legalEntities;
        EnterpriseName = enterpriseName;
        this.soluctionInterest = soluctionInterest;
        this.leadOrigin = leadOrigin;
        this.registration = registration;
        this.leadStatus = leadStatus;
        this.observations = observations;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    protected Lead() {}

    @AssertTrue(message = "A data de criação não pode ser anterior à data de atualização")
    public boolean isUpdateAtValid() {
        if (createdAt == null || updatedAt == null) return true;
        return !updatedAt.isBefore(createdAt);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LegalEntities getLegalEntities() {
        return legalEntities;
    }

    public void setLegalEntities(LegalEntities legalEntities) {
        this.legalEntities = legalEntities;
    }

    public String getEnterpriseName() {
        return EnterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        EnterpriseName = enterpriseName;
    }

    public SolutionInterest getSoluctionInterest() {
        return soluctionInterest;
    }

    public void setSoluctionInterest(SolutionInterest soluctionInterest) {
        this.soluctionInterest = soluctionInterest;
    }

    public LeadOrigin getLeadOrigin() {
        return leadOrigin;
    }

    public void setLeadOrigin(LeadOrigin leadOrigin) {
        this.leadOrigin = leadOrigin;
    }

    public Registrarion getRegistration() {
        return registration;
    }

    public void setRegistration(Registrarion registration) {
        this.registration = registration;
    }

    public String getLeadStatus() {
        return leadStatus;
    }

    public void setLeadStatus(String leadStatus) {
        this.leadStatus = leadStatus;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
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
        if (!(o instanceof Lead lead)) return false;
        return Objects.equals(getId(), lead.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
