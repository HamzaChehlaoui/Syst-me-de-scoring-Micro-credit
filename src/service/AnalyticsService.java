package service;


import enums.Secteur;
import model.Employe;
import model.Personne;
import model.Credit;
import model.Echeance;
import repository.ClientRepository;
import repository.CreditRepository;
import repository.EcheanceRepository;
import repository.IncidentRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides search and aggregation analytics.
 */
public class AnalyticsService {

    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;
    private final IncidentRepository incidentRepository;
    private final EcheanceRepository echeanceRepository;

    public AnalyticsService(ClientRepository clientRepository, CreditRepository creditRepository, IncidentRepository incidentRepository, EcheanceRepository echeanceRepository) {
        this.clientRepository = clientRepository;
        this.creditRepository = creditRepository;
        this.incidentRepository = incidentRepository;
        this.echeanceRepository = echeanceRepository;
    }

    public List<Personne> searchByAgeIncomeAndMarital(int minAge, int maxAge, double minIncome, double maxIncome, String situationFamiliale) throws SQLException {
        List<Personne> all = clientRepository.findAll();
        LocalDate now = LocalDate.now();
        return all.stream().filter(p -> {
            int age = java.time.Period.between(p.getDateNaissance(), now).getYears();
            boolean ageOk = age >= minAge && age <= maxAge;
            boolean maritalOk = situationFamiliale == null || p.getSituationFamiliale().equalsIgnoreCase(situationFamiliale);
            double income = 0.0;
            if (p instanceof Employe) income = ((Employe) p).getSalaire();
            else if (p.getType().equals("PROFESSIONNEL")) income = ((model.Professionnel) p).getRevenu();
            boolean incomeOk = income >= minIncome && income <= maxIncome;
            return ageOk && maritalOk && incomeOk;
        }).collect(Collectors.toList());
    }

    public List<Personne> top10RiskyClients() throws SQLException {
        List<Personne> all = clientRepository.findAll();
        Set<Long> recentIncidentEcheance = incidentRepository.findRecentMonths(6).stream().map(Incident -> Incident.getEcheanceId()).collect(Collectors.toSet());

        List<Personne> riskyClients = new ArrayList<>();
        for (Personne p : all) {
            if (p.getScore() != null && p.getScore() < 60) {
                try {
                    if (hasRecentIncidents(p.getId(), recentIncidentEcheance)) {
                        riskyClients.add(p);
                    }
                } catch (SQLException e) {
                    // Log error and continue with next client
                    System.err.println("Error checking incidents for client " + p.getId() + ": " + e.getMessage());
                }
            }
        }

        return riskyClients.stream()
                .sorted(Comparator.comparing(p -> p.getScore(), Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toList());
    }

    private boolean hasRecentIncidents(Long clientId, Set<Long> recentIncidentEcheance) throws SQLException {
        List<Credit> credits = creditRepository.findByPersonne(clientId);

        for (Credit credit : credits) {
            List<Echeance> echeances = echeanceRepository.findByCredit(credit.getId());
            for (Echeance echeance : echeances) {
                if (recentIncidentEcheance.contains(echeance.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Map<Secteur, Double> averageScorePerSector() throws SQLException {
        List<Personne> all = clientRepository.findAll();
        Map<Secteur, List<Integer>> values = new HashMap<>();
        for (Personne p : all) {
            if (p instanceof Employe && p.getScore() != null) {
                Secteur s = ((Employe) p).getSecteur();
                if (s == null) continue;
                values.computeIfAbsent(s, k -> new ArrayList<>()).add(p.getScore());
            }
        }
        Map<Secteur, Double> avg = new HashMap<>();
        for (Map.Entry<Secteur, List<Integer>> e : values.entrySet()) {
            avg.put(e.getKey(), e.getValue().stream().mapToInt(i -> i).average().orElse(0.0));
        }
        return avg;
    }

    public List<Personne> campaignSelection() throws SQLException {
        List<Personne> all = clientRepository.findAll();
        LocalDate now = LocalDate.now();
        return all.stream().filter(p -> {
            int age = java.time.Period.between(p.getDateNaissance(), now).getYears();
            Integer score = p.getScore();
            if (score == null) return false;
            if (!(score >= 65 && score <= 85)) return false;
            if (!(age >= 28 && age <= 45)) return false;
            double income = 0.0;
            if (p instanceof Employe) income = ((Employe) p).getSalaire();
            else if (p.getType().equals("PROFESSIONNEL")) income = ((model.Professionnel) p).getRevenu();
            if (!(income >= 4000 && income <= 8000)) return false;
            return true;
        }).collect(Collectors.toList());
    }
}