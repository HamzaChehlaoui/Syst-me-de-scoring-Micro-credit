package model;



import enums.Secteur;
import enums.TypeContrat;

/**
 * Employe client profile.
 */
public class Employe extends Personne {
    private double salaire;
    private int ancienneteYears;
    private String poste;
    private TypeContrat typeContrat;
    private Secteur secteur;

    public Employe() {
        setType("EMPLOYE");
    }

    public double getSalaire() { return salaire; }
    public void setSalaire(double salaire) { this.salaire = salaire; }
    public int getAncienneteYears() { return ancienneteYears; }
    public void setAncienneteYears(int ancienneteYears) { this.ancienneteYears = ancienneteYears; }
    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }
    public TypeContrat getTypeContrat() { return typeContrat; }
    public void setTypeContrat(TypeContrat typeContrat) { this.typeContrat = typeContrat; }
    public Secteur getSecteur() { return secteur; }
    public void setSecteur(Secteur secteur) { this.secteur = secteur; }
}
