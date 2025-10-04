package ui;

import model.*;
import repository.*;
import service.*;

import java.sql.SQLException;
import java.util.*;

public class DemoView {
    private final Scanner scanner;
    private final ClientRepository clientRepository;
    private final CreditService creditService;
    private final ScoringService scoringService;
    private final EcheanceRepository echeanceRepository;

    public DemoView(Scanner scanner, ClientRepository clientRepository,
                    CreditService creditService, ScoringService scoringService,
                    EcheanceRepository echeanceRepository) {
        this.scanner = scanner;
        this.clientRepository = clientRepository;
        this.creditService = creditService;
        this.scoringService = scoringService;
        this.echeanceRepository = echeanceRepository;
    }

    public void runDemo() throws SQLException {
        System.out.println("Demo: lister clients seed, calculer score, demander un credit, generer echeances.");
        List<Personne> clients = clientRepository.findAll();
        clients.forEach(c -> System.out.println(c.getId() + " - " + c.getNom() + " " + c.getPrenom()));

        long id = readLong("Choisissez un client ID du seed: ");
        Optional<Personne> opt = clientRepository.findById(id);
        if (!opt.isPresent()) { System.out.println("Introuvable."); return; }
        Personne p = opt.get();

        boolean isExisting = true;
        double income = (p instanceof Employe) ? ((Employe) p).getSalaire() : ((Professionnel) p).getRevenu();
        int score = scoringService.computeScore(p, Collections.emptyList(), isExisting);
        clientRepository.updateScore(p.getId(), score);

        Credit cr = creditService.requestCredit(p, isExisting, score, income, 20000, 12, 0.12, "conso");
        System.out.println("Decision: " + cr.getDecision() + " Montant octroye: " + cr.getMontantOctroye());
        System.out.println("Echeances:");
        for (Echeance e : echeanceRepository.findByCredit(cr.getId())) {
            System.out.println(" - " + e.getId() + " " + e.getDateEcheance() + " " + e.getMensualite() + " " + e.getStatutPaiement());
        }
    }

    private long readLong(String label) { System.out.print(label); return Long.parseLong(scanner.nextLine().trim()); }
}
