package model;

import model.enums.TypeIncident;
import java.time.LocalDate;

public class Incident {
    private Long id;
    private LocalDate dateIncident;
    private Long echeanceId; // linked echeance
    private TypeIncident typeIncident;
    private int scoreImpact;
    private String description;

    public Incident() {}

    public Incident(LocalDate dateIncident, Long echeanceId, TypeIncident typeIncident, int scoreImpact, String description) {

        this.dateIncident = dateIncident;
        this.echeanceId = echeanceId;
        this.typeIncident = typeIncident;
        this.scoreImpact = scoreImpact;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDateIncident() { return dateIncident; }
    public void setDateIncident(LocalDate dateIncident) { this.dateIncident = dateIncident; }
    public Long getEcheanceId() { return echeanceId; }
    public void setEcheanceId(Long echeanceId) { this.echeanceId = echeanceId; }
    public TypeIncident getTypeIncident() { return typeIncident; }
    public void setTypeIncident(TypeIncident typeIncident) { this.typeIncident = typeIncident; }
    public int getScoreImpact() { return scoreImpact; }
    public void setScoreImpact(int scoreImpact) { this.scoreImpact = scoreImpact; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Incident{" +
                "id=" + id +
                ", dateIncident=" + dateIncident +
                ", echeanceId=" + echeanceId +
                ", typeIncident=" + typeIncident +
                ", scoreImpact=" + scoreImpact +
                ", description='" + description + '\'' +
                '}';
    }
}
