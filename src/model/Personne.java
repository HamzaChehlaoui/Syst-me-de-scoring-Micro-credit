package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Base class for a person (client). Specialized by Employe and Professionnel.
 */
public abstract class Personne {
    private Long id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String ville;
    private int nombreEnfants;
    private boolean investissement;
    private boolean placement;
    private String situationFamiliale; // "Marie" | "Celibataire"
    private LocalDateTime createdAt;
    private Integer score; // last known score
    private String type; // EMPLOYE | PROFESSIONNEL

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
    public boolean isInvestissement() { return investissement; }
    public void setInvestissement(boolean investissement) { this.investissement = investissement; }
    public boolean isPlacement() { return placement; }
    public void setPlacement(boolean placement) { this.placement = placement; }
    public String getSituationFamiliale() { return situationFamiliale; }
    public void setSituationFamiliale(String situationFamiliale) { this.situationFamiliale = situationFamiliale; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}