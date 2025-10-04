package ui;

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

    public PaymentView(Scanner scanner, EcheanceRepository echeanceRepository,
                       PaymentService paymentService, ClientRepository clientRepository) {
        this.scanner = scanner;
        this.echeanceRepository = echeanceRepository;
        this.paymentService = paymentService;
        this.clientRepository = clientRepository;
    }

    public void simulatePayment() throws SQLException {
        long creditId = readLong("ID credit: ");
        List<Echeance> echs = echeanceRepository.findByCredit(creditId);
        if (echs.isEmpty()) { System.out.println("Aucune echeance."); return; }

        for (Echeance e : echs) {
            System.out.println(e.getId() + " - due " + DateUtils.format(e.getDateEcheance()) +
                    " - statut " + e.getStatutPaiement() + " - montant " + e.getMensualite());
        }

        long echeanceId = readLong("Choisir ID echeance: ");
        LocalDate payDate = DateUtils.parse(readString("Date paiement (YYYY-MM-DD): "));
        long personneId = readLong("ID client (pour recalcul score): ");

        Optional<Personne> opt = clientRepository.findById(personneId);
        if (!opt.isPresent()) { System.out.println("Client introuvable."); return; }
        Personne p = opt.get();

        paymentService.pay(echeanceId, payDate, p, true, Collections.emptyList());
        System.out.println("Paiement enregistre.");
    }

    // Helpers
    private String readString(String label) { System.out.print(label); return scanner.nextLine().trim(); }
    private long readLong(String label) { System.out.print(label); return Long.parseLong(scanner.nextLine().trim()); }
}
