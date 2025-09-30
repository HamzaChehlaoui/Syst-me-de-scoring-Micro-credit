package model;

import model.enums.SecteurActivite;
import java.time.LocalDate;

public class Professionnel extends Personne {
    private double revenu;
    private String immatriculationFiscale;
    private SecteurActivite secteurActivite;
    private String activite;

    public Professionnel() { super(); }

    public Professionnel(String nom, String prenom, LocalDate dateNaissance, String ville,
                         int nombreEnfants, double investissement, double placement,
                         model.enums.SituationFamiliale situationFamiliale, LocalDate createdAt, int score,
                         double revenu, String immatriculationFiscale, SecteurActivite secteurActivite, String activite) {
        super(nom, prenom, dateNaissance, ville, nombreEnfants, investissement, placement, situationFamiliale, createdAt, score);
        this.revenu = revenu;
        this.immatriculationFiscale = immatriculationFiscale;
        this.secteurActivite = secteurActivite;
        this.activite = activite;
    }

    public double getRevenu() { return revenu; }
    public void setRevenu(double revenu) { this.revenu = revenu; }
    public String getImmatriculationFiscale() { return immatriculationFiscale; }
    public void setImmatriculationFiscale(String immatriculationFiscale) { this.immatriculationFiscale = immatriculationFiscale; }
    public SecteurActivite getSecteurActivite() { return secteurActivite; }
    public void setSecteurActivite(SecteurActivite secteurActivite) { this.secteurActivite = secteurActivite; }
    public String getActivite() { return activite; }
    public void setActivite(String activite) { this.activite = activite; }

    @Override
    public String toString() {
        return "Professionnel{" +
                "revenu=" + revenu +
                ", immatriculationFiscale='" + immatriculationFiscale + '\'' +
                ", secteurActivite=" + secteurActivite +
                ", activite='" + activite + '\'' +
                "} " + super.toString();
    }
}
