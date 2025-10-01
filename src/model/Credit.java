package model;

import model.enums.DecisionCredit;
import model.enums.TypeCredit;
import java.time.LocalDate;

public class Credit {
    private Long id;
    private Long clientId; // reference to Personne.id
    private LocalDate dateCredit;
    private double montantDemande;
    private double montantOctroye;
    private double tauxInteret;
    private int dureeMois;
    private TypeCredit typeCredit;
    private DecisionCredit decision;

    public Credit() { this.dateCredit = LocalDate.now(); }

    public Credit(Long clientId, LocalDate dateCredit, double montantDemande, double montantOctroye,
                  double tauxInteret, int dureeMois, TypeCredit typeCredit, DecisionCredit decision) {

        this.clientId = clientId;
        this.dateCredit = dateCredit == null ? LocalDate.now() : dateCredit;
        this.montantDemande = montantDemande;
        this.montantOctroye = montantOctroye;
        this.tauxInteret = tauxInteret;
        this.dureeMois = dureeMois;
        this.typeCredit = typeCredit;
        this.decision = decision;
    }

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public LocalDate getDateCredit() { return dateCredit; }
    public void setDateCredit(LocalDate dateCredit) { this.dateCredit = dateCredit; }
    public double getMontantDemande() { return montantDemande; }
    public void setMontantDemande(double montantDemande) { this.montantDemande = montantDemande; }
    public double getMontantOctroye() { return montantOctroye; }
    public void setMontantOctroye(double montantOctroye) { this.montantOctroye = montantOctroye; }
    public double getTauxInteret() { return tauxInteret; }
    public void setTauxInteret(double tauxInteret) { this.tauxInteret = tauxInteret; }
    public int getDureeMois() { return dureeMois; }
    public void setDureeMois(int dureeMois) { this.dureeMois = dureeMois; }
    public String getTypeCredit() { return typeCredit; }
    public void setTypeCredit(TypeCredit typeCredit) { this.typeCredit = typeCredit; }
    public String getDecision() { return decision; }
    public void setDecision(DecisionCredit decision) { this.decision = decision; }

    @Override
    public String toString() {
        return "Credit{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", dateCredit=" + dateCredit +
                ", montantDemande=" + montantDemande +
                ", montantOctroye=" + montantOctroye +
                ", tauxInteret=" + tauxInteret +
                ", dureeMois=" + dureeMois +
                ", typeCredit=" + typeCredit +
                ", decision=" + decision +
                '}';
    }
}
