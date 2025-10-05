package service;


import enums.*;
import model.*;
import repository.AuditRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


public class ScoringService {

    private final AuditRepository auditRepository;

    public ScoringService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public int computeScore(Personne p, List<Incident> incidents, boolean isExistingClient) throws SQLException {
        int score = 0;

        int stabilite = computeStabilite(p);
        score += stabilite;
        audit("Personne", p.getId(), "SCORING_STABILITE", null, String.valueOf(stabilite), "Stabilite Professionnelle");

        int capacite = computeCapaciteFinanciere(p);
        score += capacite;
        audit("Personne", p.getId(), "SCORING_CAPACITE", null, String.valueOf(capacite), "Capacite financiere");

        int historique = computeHistorique(incidents);
        score += historique;
        audit("Personne", p.getId(), "SCORING_HISTORIQUE", null, String.valueOf(historique), "Historique incidents");

        int relation = computeRelationClient(p, isExistingClient);
        score += relation;
        audit("Personne", p.getId(), "SCORING_RELATION", null, String.valueOf(relation), "Relation client");

        int complementaires = (p.isInvestissement() || p.isPlacement()) ? 10 : 0;
        score += complementaires;
        audit("Personne", p.getId(), "SCORING_COMPLEMENTAIRES", null, String.valueOf(complementaires), "Patrimoine");

        score = Math.max(0, Math.min(100, score));
        audit("Personne", p.getId(), "SCORING_TOTAL", Objects.toString(p.getScore(), null), String.valueOf(score), "Score total calcule");
        return score;
    }

    private int computeStabilite(Personne p) {
        int points = 0;
        if (p instanceof Employe) {
            Employe e = (Employe) p;
            if (e.getTypeContrat() == TypeContrat.CDI_PUBLIC) points = 25;
            else if (e.getTypeContrat() == TypeContrat.CDI_PRIVE_GRANDE_ENTREPRISE) points = 15;
            else if (e.getTypeContrat() == TypeContrat.CDI_PRIVE_PME) points = 12;
            else if (e.getTypeContrat() == TypeContrat.CDD_INTERIM) points = 10;
            if (e.getSecteur() == Secteur.PUBLIC) {
            }
            if (e.getAncienneteYears() >= 5) points += 5;
            else if (e.getAncienneteYears() >= 2) points += 3;
            else if (e.getAncienneteYears() >= 1) points += 1;
        } else if (p instanceof Professionnel) {
            Professionnel pr = (Professionnel) p;
            String secteurActivite = pr.getSecteurActivite() == null ? "" : pr.getSecteurActivite().toLowerCase();
            if (secteurActivite.contains("lib")) points = 18;
            else points = 12;
        }
        return Math.min(points, 30);
    }

    private int computeCapaciteFinanciere(Personne p) {
        double monthly = 0.0;
        if (p instanceof Employe) monthly = ((Employe) p).getSalaire();
        else if (p instanceof Professionnel) monthly = ((Professionnel) p).getRevenu();

        if (monthly >= 10000) return 30;
        if (monthly >= 8000) return 25;
        if (monthly >= 5000) return 20;
        if (monthly >= 3000) return 15;
        return 10;
    }

    private int computeHistorique(List<Incident> incidents) {
        if (incidents == null || incidents.isEmpty()) {
            return 10;
        }

        int points = 0;

        // Count different types of incidents
        long impayesNonRegles = incidents.stream()
                .filter(i -> i.getTypeIncident() == IncidentType.IMPAYE)
                .filter(i -> !i.isRegle())
                .count();

        long impayesRegles = incidents.stream()
                .filter(i -> i.getTypeIncident() == IncidentType.IMPAYE)
                .filter(i -> i.isRegle())
                .count();

        long retardsNonRegles = incidents.stream()
                .filter(i -> i.getTypeIncident() == IncidentType.RETARD)
                .filter(i -> !i.isRegle())
                .count();

        long retardsRegles = incidents.stream()
                .filter(i -> i.getTypeIncident() == IncidentType.RETARD)
                .filter(i -> i.isRegle())
                .count();

        // Apply rules

        // 1. Unpaid incidents: -10 pts
        if (impayesNonRegles > 0) {
            points -= 10;
        }

        // 2. Paid incidents (resolved): +5 pts
        if (impayesRegles > 0) {
            points += 5;
        }

        // 3. 1-3 occasional late payments (unpaid): -3 pts
        if (retardsNonRegles >= 1 && retardsNonRegles <= 3) {
            points -= 3;
        }

        // 4. Regular late payments ≥4 (unpaid): -5 pts
        if (retardsNonRegles >= 4) {
            points -= 5;
        }

        // 5. Late but paid: +3 pts
        if (retardsRegles > 0) {
            points += 3;
        }

        // Limit between -15 and +15
        return Math.max(-15, Math.min(15, points));
    }
    private int computeRelationClient(Personne p, boolean isExisting) {


        int age = java.time.Period.between(p.getDateNaissance(), java.time.LocalDate.now()).getYears();
        if (!isExisting) {
            int ptsAge = (age >= 18 && age <= 25) ? 4 : (age <= 35 ? 8 : (age <= 55 ? 10 : 6));
            int ptsFamiliale = "Marie".equalsIgnoreCase(p.getSituationFamiliale()) ? 3 : 2;
            int children = p.getNombreEnfants();
            int ptsEnfants = children == 0 ? 2 : (children <= 2 ? 1 : 0);
            return Math.min(10, ptsAge + ptsFamiliale + ptsEnfants);
        } else {
            return 8;
        }
    }

    private void audit(String entityType, Long entityId, String field, String oldValue, String newValue, String reason) throws SQLException {
        AuditRecord ar = new AuditRecord();
        ar.setEntityType(entityType);
        ar.setEntityId(entityId);
        ar.setField(field);
        ar.setOldValue(oldValue);
        ar.setNewValue(newValue);
        ar.setChangedAt(LocalDateTime.now());
        ar.setReason(reason);
        auditRepository.save(ar);
    }
}
