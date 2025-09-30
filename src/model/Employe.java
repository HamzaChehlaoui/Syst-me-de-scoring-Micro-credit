package model;

import model.enums.Secteur;
import model.enums.TypeContrat;
import java.time.LocalDate;

public class Employe extends Personne {
    private double salaire;
    private int anciennete; // en mois ou années (consistency with service)
    private String poste;
    private TypeContrat typeContrat;
    private Secteur secteur;

    public Employe() { super(); }

    public Employe(String nom, String prenom, LocalDate dateNaissance, String ville,
                   int nombreEnfants, double investissement, double placement,
                   model.enums.SituationFamiliale situationFamiliale, LocalDate createdAt, int score,
                   double salaire, int anciennete, String poste, TypeContrat typeContrat, Secteur secteur) {
        super( nom, prenom, dateNaissance, ville, nombreEnfants, investissement, placement, situationFamiliale, createdAt, score);
        this.salaire = salaire;
        this.anciennete = anciennete;
        this.poste = poste;
        this.typeContrat = typeContrat;
        this.secteur = secteur;
    }

    public double getSalaire() { return salaire; }
    public void setSalaire(double salaire) { this.salaire = salaire; }
    public int getAnciennete() { return anciennete; }
    public void setAnciennete(int anciennete) { this.anciennete = anciennete; }
    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }
    public TypeContrat getTypeContrat() { return typeContrat; }
    public void setTypeContrat(TypeContrat typeContrat) { this.typeContrat = typeContrat; }
    public Secteur getSecteur() { return secteur; }
    public void setSecteur(Secteur secteur) { this.secteur = secteur; }

    @Override
    public String toString() {
        return "Employe{" +
                "salaire=" + salaire +
                ", anciennete=" + anciennete +
                ", poste='" + poste + '\'' +
                ", typeContrat=" + typeContrat +
                ", secteur=" + secteur +
                "} " + super.toString();
    }
}
