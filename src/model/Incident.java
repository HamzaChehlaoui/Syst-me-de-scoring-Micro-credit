package model;

import enums.IncidentType;
import java.time.LocalDate;

public class Incident {
    private Long id;
    private Long echeanceId;
    private LocalDate dateIncident;
    private IncidentType typeIncident;
    private int impactScore;
    private String note;
    private boolean regle;

    // Constructors
    public Incident() {
    }

    public Incident(Long id, Long echeanceId, LocalDate dateIncident,
                    IncidentType typeIncident, int impactScore, String note, boolean regle) {
        this.id = id;
        this.echeanceId = echeanceId;
        this.dateIncident = dateIncident;
        this.typeIncident = typeIncident;
        this.impactScore = impactScore;
        this.note = note;
        this.regle = regle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEcheanceId() {
        return echeanceId;
    }

    public void setEcheanceId(Long echeanceId) {
        this.echeanceId = echeanceId;
    }

    public LocalDate getDateIncident() {
        return dateIncident;
    }

    public void setDateIncident(LocalDate dateIncident) {
        this.dateIncident = dateIncident;
    }

    public IncidentType getTypeIncident() {
        return typeIncident;
    }

    public void setTypeIncident(IncidentType typeIncident) {
        this.typeIncident = typeIncident;
    }

    public int getImpactScore() {
        return impactScore;
    }

    public void setImpactScore(int impactScore) {
        this.impactScore = impactScore;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isRegle() {
        return regle;
    }

    public void setRegle(boolean regle) {
        this.regle = regle;
    }

    @Override
    public String toString() {
        return "Incident{" +
                "id=" + id +
                ", echeanceId=" + echeanceId +
                ", dateIncident=" + dateIncident +
                ", typeIncident=" + typeIncident +
                ", impactScore=" + impactScore +
                ", regle=" + regle +
                ", note='" + note + '\'' +
                '}';
    }
}