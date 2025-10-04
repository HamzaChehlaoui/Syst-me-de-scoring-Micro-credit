package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Credit demande et decision.
 */
public class Credit {
    private Long id;
    private Long personneId;
    private LocalDate dateCredit;
    private double montantDemande;
    private Double montantOctroye;
    private double tauxInteret;
    private int dureeMois;
    private String typeCredit;
    private String decision;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPersonneId() { return personneId; }
    public void setPersonneId(Long personneId) { this.personneId = personneId; }
    public LocalDate getDateCredit() { return dateCredit; }
    public void setDateCredit(LocalDate dateCredit) { this.dateCredit = dateCredit; }
    public double getMontantDemande() { return montantDemande; }
    public void setMontantDemande(double montantDemande) { this.montantDemande = montantDemande; }
    public Double getMontantOctroye() { return montantOctroye; }
    public void setMontantOctroye(Double montantOctroye) { this.montantOctroye = montantOctroye; }
    public double getTauxInteret() { return tauxInteret; }
    public void setTauxInteret(double tauxInteret) { this.tauxInteret = tauxInteret; }
    public int getDureeMois() { return dureeMois; }
    public void setDureeMois(int dureeMois) { this.dureeMois = dureeMois; }
    public String getTypeCredit() { return typeCredit; }
    public void setTypeCredit(String typeCredit) { this.typeCredit = typeCredit; }
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
