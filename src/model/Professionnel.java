package model;

/**
 * Professionnel (independant / auto-entrepreneur / profession liberale).
 */
public class Professionnel extends Personne {
    private double revenu;
    private String immatriculationFiscale;
    private String secteurActivite;
    private String activite;

    public Professionnel() {
        setType("PROFESSIONNEL");
    }

    public double getRevenu() { return revenu; }
    public void setRevenu(double revenu) { this.revenu = revenu; }
    public String getImmatriculationFiscale() { return immatriculationFiscale; }
    public void setImmatriculationFiscale(String immatriculationFiscale) { this.immatriculationFiscale = immatriculationFiscale; }
    public String getSecteurActivite() { return secteurActivite; }
    public void setSecteurActivite(String secteurActivite) { this.secteurActivite = secteurActivite; }
    public String getActivite() { return activite; }
    public void setActivite(String activite) { this.activite = activite; }
}
