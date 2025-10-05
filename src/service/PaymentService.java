package service;

import enums.IncidentType;
import enums.PaymentStatus;
import model.Echeance;
import model.Incident;
import model.Personne;
import repository.ClientRepository;
import repository.EcheanceRepository;
import repository.IncidentRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class PaymentService {

    private final EcheanceRepository echeanceRepository;
    private final IncidentRepository incidentRepository;
    private final ClientRepository clientRepository;
    private final ScoringService scoringService;

    public PaymentService(EcheanceRepository echeanceRepository,
                          IncidentRepository incidentRepository,
                          ClientRepository clientRepository,
                          ScoringService scoringService) {
        this.echeanceRepository = echeanceRepository;
        this.incidentRepository = incidentRepository;
        this.clientRepository = clientRepository;
        this.scoringService = scoringService;
    }

    /**
     * Simulate payment for an echeance and update status and incidents.
     * Rules:
     * - On time (diff <= 4): PAYE_A_TEMPS, no incident
     * - Late 5-30 days: EN_RETARD, create RETARD incident (regle=true)
     * - Late 31+ days: IMPAYE, create IMPAYE incident (regle=false)
     */
    public void pay(long echeanceId, LocalDate paymentDate, Personne personne,
                    boolean isExisting, java.util.List<Incident> historicalForPerson) throws SQLException {

        Optional<Echeance> opt = echeanceRepository.findById(echeanceId);
        if (!opt.isPresent()) {
            throw new IllegalArgumentException("Echeance not found");
        }

        Echeance e = opt.get();
        long diff = ChronoUnit.DAYS.between(e.getDateEcheance(), paymentDate);
        PaymentStatus status;
        Incident incident = null;

        System.out.println("Date echeance: " + e.getDateEcheance());
        System.out.println("Date paiement: " + paymentDate);
        System.out.println("Retard: " + diff + " jours");

        if (diff <= 4) {
            status = PaymentStatus.PAYE_A_TEMPS;
            System.out.println("Paiement a temps (pas d'incident)");

        } else if (diff >= 5 && diff <= 30) {
            status = PaymentStatus.EN_RETARD;
            incident = new Incident();
            incident.setDateIncident(paymentDate);
            incident.setEcheanceId(echeanceId);
            incident.setTypeIncident(IncidentType.RETARD);
            incident.setImpactScore(-3);
            incident.setRegle(true);
            incident.setNote("Paiement en retard de " + diff + " jours");
            System.out.println("Retard de " + diff + " jours (incident RETARD cree, regle)");

        } else {

            status = PaymentStatus.IMPAYE;
            incident = new Incident();
            incident.setDateIncident(paymentDate);
            incident.setEcheanceId(echeanceId);
            incident.setTypeIncident(IncidentType.IMPAYE);
            incident.setImpactScore(-10);
            incident.setRegle(false);
            incident.setNote("Impaye " + diff + " jours de retard");
            System.out.println("Impaye de " + diff + " jours (incident IMPAYE cree, non regle)");
        }

        // Update payment status
        echeanceRepository.updatePayment(e.getId(), paymentDate, status);

        // If incident created, save it and recalculate score
        if (incident != null) {
            incidentRepository.create(incident);
            System.out.println("Incident enregistre avec ID: " + incident.getId());

            // Add new incident to historical list
            java.util.List<Incident> updatedList = new java.util.ArrayList<>(historicalForPerson);
            updatedList.add(incident);

            // Recalculate score
            int oldScore = personne.getScore();
            int newScore = scoringService.computeScore(personne, updatedList, isExisting);
            clientRepository.updateScore(personne.getId(), newScore);

            System.out.println("Score mis a jour: " + oldScore + " → " + newScore);
        }
    }


    public void regularizeIncident(long incidentId, Personne personne, boolean isExisting) throws SQLException {
        Optional<Incident> opt = incidentRepository.findById(incidentId);
        if (!opt.isPresent()) {
            throw new IllegalArgumentException("Incident not found");
        }

        Incident incident = opt.get();
        if (incident.isRegle()) {
            System.out.println("Cet incident est deja regle");
            return;
        }

        // Mark as resolved
        incidentRepository.updateRegle(incidentId, true);
        incident.setRegle(true);

        System.out.println("Incident ID " + incidentId + " marque comme regle");

        // Recalculate score
        java.util.List<Incident> allIncidents = incidentRepository.findByPersonneId(personne.getId());
        int oldScore = personne.getScore();
        int newScore = scoringService.computeScore(personne, allIncidents, isExisting);
        clientRepository.updateScore(personne.getId(), newScore);

        System.out.println("Score mis a jour: " + oldScore + " → " + newScore);
    }
}