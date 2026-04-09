package br.com.vendemais.domain.entity;

import br.com.vendemais.domain.enums.*;
import jakarta.persistence.*;
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
    private String phone;

    @NotBlank(message = "Email não pode ser vazio")
    @Email
    private String email;

    private PersonType personType;

    private String companyName;

    private Solution interestSolution;

    @NotNull(message = "Origem do lead não pode ser vazia")
    private LeadSource leadSource;

    @NotNull (message = "Forma de registro do lead não pode ser vazia")
    private EntryMethod entryMethod;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    public Lead(String name, String phone, String email, PersonType personType, String companyName, Solution interestSolution, LeadSource leadSource, EntryMethod entryMethod, String notes) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.personType = personType;
        this.companyName = companyName;
        this.interestSolution = interestSolution;
        this.leadSource = leadSource;
        this.entryMethod = entryMethod;
        this.notes = notes;
        this.createdAt = LocalDate.now();
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PersonType getPersonType() {
        return personType;
    }

    public void setPersonType(PersonType personType) {
        this.personType = personType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        companyName = companyName;
    }

    public Solution getInterestSolution() {
        return interestSolution;
    }

    public void setInterestSoluction(Solution soluctionInterest) {
        this.interestSolution = soluctionInterest;
    }

    public LeadSource getLeadSource() {
        return leadSource;
    }

    public void setLeadSource(LeadSource leadSource) {
        this.leadSource = leadSource;
    }

    public EntryMethod getEntryMethod() {
        return entryMethod;
    }

    public void setEntryMethod(EntryMethod entryMethod) {
        this.entryMethod = entryMethod;
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
        if (!(o instanceof Lead lead)) return false;
        return Objects.equals(getId(), lead.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
