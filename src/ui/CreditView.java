package ui;

import model.*;
import repository.*;
import service.*;

import java.sql.SQLException;
import java.util.*;

public class CreditView {
    private final Scanner scanner;
    private final ClientRepository clientRepository;
    private final CreditService creditService;
    private final AuditRepository auditRepository;
    private final ScoringService scoringService;

    public CreditView(Scanner scanner, ClientRepository clientRepository,
                      CreditService creditService, AuditRepository auditRepository,
                      ScoringService scoringService) {
        this.scanner = scanner;
        this.clientRepository = clientRepository;
        this.creditService = creditService;
        this.auditRepository = auditRepository;
        this.scoringService = scoringService;
    }

    public void requestCredit() throws SQLException {
        System.out.print("ID client: ");
        long id = Long.parseLong(scanner.nextLine().trim());
        Optional<Personne> opt = clientRepository.findById(id);
        if (!opt.isPresent()) { System.out.println("Introuvable."); return; }
        Personne p = opt.get();

        boolean isExisting = readYesNo("Client existant ? (y/n): ");
        double income = (p instanceof Employe) ? ((Employe) p).getSalaire() : ((Professionnel) p).getRevenu();
        int score = scoringService.computeScore(p, Collections.emptyList(), isExisting);
        clientRepository.updateScore(p.getId(), score);

        double requested = readDouble("Montant demande (DH): ");
        int months = readInt("Duree (mois): ");
        double annualRate = readDouble("Taux interet annuel (ex 0.12 pour 12%): ");
        System.out.print("Type credit (conso/habitat/...): ");
        String typeCredit = scanner.nextLine().trim();

        Credit cr = creditService.requestCredit(p, isExisting, score, income, requested, months, annualRate, typeCredit);
        System.out.println("Decision: " + cr.getDecision() + " | Montant octroye: " + cr.getMontantOctroye());
    }

    // Helpers
    private int readInt(String label) { System.out.print(label); return Integer.parseInt(scanner.nextLine().trim()); }
    private double readDouble(String label) { System.out.print(label); return Double.parseDouble(scanner.nextLine().trim()); }
    private boolean readYesNo(String label) {
        System.out.print(label);
        String s = scanner.nextLine().trim().toLowerCase();
        return s.startsWith("y") || s.equals("oui") || s.equals("o");
    }
}
