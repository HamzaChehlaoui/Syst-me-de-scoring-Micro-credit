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

/**
 * Handles simulated payments and incident creation.
 */
public class PaymentService {

    private final EcheanceRepository echeanceRepository;
    private final IncidentRepository incidentRepository;
    private final ClientRepository clientRepository;
    private final ScoringService scoringService;

    public PaymentService(EcheanceRepository echeanceRepository, IncidentRepository incidentRepository, ClientRepository clientRepository, ScoringService scoringService) {
        this.echeanceRepository = echeanceRepository;
        this.incidentRepository = incidentRepository;
        this.clientRepository = clientRepository;
        this.scoringService = scoringService;
    }

    /**
     * Simulate payment for an echeance and update status and incidents.
     * - Retard: 5–30 days after due date => EN_RETARD
     * - Impaye: 31+ days after due date => IMPAYE
     */
    public void pay(long echeanceId, LocalDate paymentDate, Personne personne, boolean isExisting, java.util.List<Incident> historicalForPerson) throws SQLException {
        Optional<Echeance> opt = echeanceRepository.findById(echeanceId);
        if (!opt.isPresent()) throw new IllegalArgumentException("Echeance not found");
        Echeance e = opt.get();
        long diff = ChronoUnit.DAYS.between(e.getDateEcheance(), paymentDate);
        PaymentStatus status;
        Incident incident = null;

        if (diff <= 0) {
            status = PaymentStatus.PAYE_A_TEMPS;
        } else if (diff >= 5 && diff <= 30) {
            status = PaymentStatus.EN_RETARD;
            incident = new Incident();
            incident.setDateIncident(paymentDate);
            incident.setEcheanceId(echeanceId);
            incident.setTypeIncident(IncidentType.RETARD);
            incident.setImpactScore(-3);
            incident.setNote("Paiement en retard de " + diff + " jours");
        } else {
            status = PaymentStatus.IMPAYE;
            incident = new Incident();
            incident.setDateIncident(paymentDate);
            incident.setEcheanceId(echeanceId);
            incident.setTypeIncident(IncidentType.IMPAYE);
            incident.setImpactScore(-10);
            incident.setNote("Impayé " + diff + " jours de retard");
        }

        echeanceRepository.updatePayment(e.getId(), paymentDate, status);
        if (incident != null) {
            incidentRepository.create(incident);
            // re-evaluate score
            int newScore = scoringService.computeScore(personne, historicalForPerson, isExisting);
            clientRepository.updateScore(personne.getId(), newScore);
        }
    }
}
