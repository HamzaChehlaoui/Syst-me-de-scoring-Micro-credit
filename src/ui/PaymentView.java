package ui;

import enums.PaymentStatus;
import model.*;
import repository.*;
import service.*;
import utils.DateUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class PaymentView {
    private final Scanner scanner;
    private final EcheanceRepository echeanceRepository;
    private final PaymentService paymentService;
    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;
    private final IncidentRepository incidentRepository;

    public PaymentView(Scanner scanner, EcheanceRepository echeanceRepository,
                       PaymentService paymentService, ClientRepository clientRepository, CreditRepository creditRepository, IncidentRepository incidentRepository) {
        this.scanner = scanner;
        this.echeanceRepository = echeanceRepository;
        this.paymentService = paymentService;
        this.clientRepository = clientRepository;
        this.creditRepository = creditRepository;
        this.incidentRepository = incidentRepository;
    }

    public void simulatePayment() throws SQLException {
        long creditId = readLong("ID credit: ");

        Optional<Credit> creditOpt = creditRepository.findById(creditId);
        if (!creditOpt.isPresent()) {
            System.out.println("Credit introuvable.");
            return;
        }
        Credit credit = creditOpt.get();

        long personneId = credit.getPersonneId();


        Optional<Personne> opt = clientRepository.findById(personneId);
        if (!opt.isPresent()) {
            System.out.println("Client introuvable.");
            return;
        }
        Personne p = opt.get();

        System.out.println("Client: " + p.getNom() + " " + p.getPrenom() +
                " (Score actuel: " + p.getScore() + ")");

        List<Echeance> allEchs = echeanceRepository.findByCredit(creditId);

        List<Echeance> echs = allEchs.stream()
                .filter(e -> e.getStatutPaiement() == PaymentStatus.A_PAYER)
                .collect(java.util.stream.Collectors.toList());

        if (echs.isEmpty()) {
            System.out.println("Aucune echeance en attente. Toutes sont payees!");
            return;
        }

        System.out.println("\nEcheances en attente de paiement:");
        for (Echeance e : echs) {
            System.out.println("  ID: " + e.getId() +
                    " | Due: " + DateUtils.format(e.getDateEcheance()) +
                    " | Statut: " + e.getStatutPaiement() +
                    " | Montant: " + e.getMensualite() + " DH");
        }

        long echeanceId = readLong("\nChoisir ID echeance: ");

        boolean validEcheance = echs.stream().anyMatch(e -> e.getId() == echeanceId);
        if (!validEcheance) {
            System.out.println("Echeance invalide pour ce credit.");
            return;
        }

        LocalDate payDate = DateUtils.parse(readString("Date paiement (YYYY-MM-DD): "));

        java.util.List<Incident> historicalIncidents = incidentRepository.findByPersonneId(personneId);
        System.out.println("Incidents historiques: " + historicalIncidents.size());

        paymentService.pay(echeanceId, payDate, p, true, historicalIncidents);

        Optional<Personne> updatedPerson = clientRepository.findById(personneId);
        if (updatedPerson.isPresent()) {
            System.out.println("Paiement enregistre.");
            System.out.println("Nouveau score: " + updatedPerson.get().getScore());
        }
    }

    // Helpers
    private String readString(String label) { System.out.print(label); return scanner.nextLine().trim(); }
    private long readLong(String label) { System.out.print(label); return Long.parseLong(scanner.nextLine().trim()); }
}
