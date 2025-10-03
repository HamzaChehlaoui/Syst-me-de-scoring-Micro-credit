package model;

import java.time.LocalDate;
import enums.IncidentType;

/**
 * Incident lie au paiement.
 */
public class Incident {
    private Long id;
    private LocalDate dateIncident;
    private Long echeanceId;
    private IncidentType typeIncident;
    private int impactScore;
    private String note;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDateIncident() { return dateIncident; }
    public void setDateIncident(LocalDate dateIncident) { this.dateIncident = dateIncident; }
    public Long getEcheanceId() { return echeanceId; }
    public void setEcheanceId(Long echeanceId) { this.echeanceId = echeanceId; }
    public IncidentType getTypeIncident() { return typeIncident; }
    public void setTypeIncident(IncidentType typeIncident) { this.typeIncident = typeIncident; }
    public int getImpactScore() { return impactScore; }
    public void setImpactScore(int impactScore) { this.impactScore = impactScore; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
