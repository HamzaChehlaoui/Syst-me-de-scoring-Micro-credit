package model;

import model.enums.SituationFamiliale;
import java.time.LocalDate;
import java.util.Objects;

public abstract class Personne {
    private Long id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String ville;
    private int nombreEnfants;
    private double investissement;
    private double placement;
    private SituationFamiliale situationFamiliale;
    private LocalDate createdAt;
    private int score;

    public Personne() { this.createdAt = LocalDate.now(); }

    public Personne(String nom, String prenom, LocalDate dateNaissance, String ville,
                    int nombreEnfants, double investissement, double placement,
                    SituationFamiliale situationFamiliale, LocalDate createdAt, int score) {

        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.ville = ville;
        this.nombreEnfants = nombreEnfants;
        this.investissement = investissement;
        this.placement = placement;
        this.situationFamiliale = situationFamiliale;
        this.createdAt = createdAt == null ? LocalDate.now() : createdAt;
        this.score = score;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public int getNombreEnfants() { return nombreEnfants; }
    public void setNombreEnfants(int nombreEnfants) { this.nombreEnfants = nombreEnfants; }
    public double getInvestissement() { return investissement; }
    public void setInvestissement(double investissement) { this.investissement = investissement; }
    public double getPlacement() { return placement; }
    public void setPlacement(double placement) { this.placement = placement; }
    public SituationFamiliale getSituationFamiliale() { return situationFamiliale; }
    public void setSituationFamiliale(SituationFamiliale situationFamiliale) { this.situationFamiliale = situationFamiliale; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    @Override
    public String toString() {
        return "Personne{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", dateNaissance=" + dateNaissance +
                ", ville='" + ville + '\'' +
                ", nombreEnfants=" + nombreEnfants +
                ", investissement=" + investissement +
                ", placement=" + placement +
                ", situationFamiliale=" + situationFamiliale +
                ", createdAt=" + createdAt +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Personne personne = (Personne) o;
        return Objects.equals(id, personne.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
