package ui;


import enums.*;
import model.*;
import repository.*;
import service.*;
import utils.DateUtils;
import utils.ValidationUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * CLI main menu.
 */
public class MainApp {

    private final Scanner scanner = new Scanner(System.in);

    private final ClientRepository clientRepository = new ClientRepository();
    private final CreditRepository creditRepository = new CreditRepository();
    private final EcheanceRepository echeanceRepository = new EcheanceRepository();
    private final IncidentRepository incidentRepository = new IncidentRepository();
    private final AuditRepository auditRepository = new AuditRepository();

    private final ScoringService scoringService = new ScoringService(auditRepository);
    private final CreditService creditService = new CreditService(creditRepository, echeanceRepository);
    private final PaymentService paymentService = new PaymentService(echeanceRepository, incidentRepository, clientRepository, scoringService);
    private final AnalyticsService analyticsService = new AnalyticsService(clientRepository, creditRepository, incidentRepository);

    public static void main(String[] args) {
        new MainApp().run();
    }

    private void run() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1": createClient(); break;
                    case "2": editClient(); break;
                    case "3": viewClient(); break;
                    case "4": deleteClient(); break;
                    case "5": listClients(); break;
                    case "6": requestCredit(); break;
                    case "7": simulatePayment(); break;
                    case "8": runAnalytics(); break;
                    case "9": runDemo(); break;
                    case "0": System.out.println("Au revoir!"); return;
                    default: System.out.println("Choix invalide.");
                }
            } catch (Exception ex) {
                System.out.println("Erreur: " + ex.getMessage());
                ex.printStackTrace(System.out);
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== Microfinance CLI ===");
        System.out.println("1. Create client (Employe/Professionnel)");
        System.out.println("2. Edit client");
        System.out.println("3. View client profile (score & audit)");
        System.out.println("4. Delete client");
        System.out.println("5. List all clients (pagination)");
        System.out.println("6. Request credit");
        System.out.println("7. Simulate payment for an echeance");
        System.out.println("8. Run analytics reports");
        System.out.println("9. Run demo scenario");
        System.out.println("0. Exit");
        System.out.print("Votre choix: ");
    }

    private void createClient() throws SQLException {
        System.out.print("Type (1=Employe, 2=Professionnel): ");
        String type = scanner.nextLine().trim();
        Personne p;
        if ("1".equals(type)) {
            p = new Employe();
            fillBase(p);
            Employe e = (Employe) p;
            e.setSalaire(readDouble("Salaire mensuel (DH): "));
            e.setAncienneteYears(readInt("Anciennete (annees): "));
            System.out.print("Poste: "); e.setPoste(scanner.nextLine().trim());
            e.setTypeContrat(readTypeContrat());
            e.setSecteur(readSecteur());
        } else {
            p = new Professionnel();
            fillBase(p);
            Professionnel pr = (Professionnel) p;
            pr.setRevenu(readDouble("Revenu mensuel (DH): "));
            System.out.print("Immatriculation fiscale: "); pr.setImmatriculationFiscale(scanner.nextLine().trim());
            System.out.print("Secteur d'activite: "); pr.setSecteurActivite(scanner.nextLine().trim());
            System.out.print("Activite: "); pr.setActivite(scanner.nextLine().trim());
        }
        clientRepository.create(p);
        System.out.println("Client cree avec id=" + p.getId());
    }

    private void editClient() throws SQLException {
        long id = readLong("ID client: ");
        Optional<Personne> opt = clientRepository.findById(id);
        if (!opt.isPresent()) { System.out.println("Introuvable."); return; }
        Personne p = opt.get();
        fillBase(p);
        if (p instanceof Employe) {
            Employe e = (Employe) p;
            e.setSalaire(readDouble("Salaire mensuel (DH): "));
            e.setAncienneteYears(readInt("Anciennete (annees): "));
            System.out.print("Poste: "); e.setPoste(scanner.nextLine().trim());
            e.setTypeContrat(readTypeContrat());
            e.setSecteur(readSecteur());
        } else {
            Professionnel pr = (Professionnel) p;
            pr.setRevenu(readDouble("Revenu mensuel (DH): "));
            System.out.print("Immatriculation fiscale: "); pr.setImmatriculationFiscale(scanner.nextLine().trim());
            System.out.print("Secteur d'activite: "); pr.setSecteurActivite(scanner.nextLine().trim());
            System.out.print("Activite: "); pr.setActivite(scanner.nextLine().trim());
        }
        clientRepository.update(p);
        System.out.println("Client mis a jour.");
    }

    private void viewClient() throws SQLException {
        long id = readLong("ID client: ");
        Optional<Personne> opt = clientRepository.findById(id);
        if (!opt.isPresent()) { System.out.println("Introuvable."); return; }
        Personne p = opt.get();
        System.out.println("Client: " + p.getNom() + " " + p.getPrenom() + " (" + p.getType() + ")");
        System.out.println("Score: " + p.getScore());
        System.out.println("Audit:");
        for (AuditRecord ar : new AuditRepository().findByEntity("Personne", p.getId())) {
            System.out.println(" - " + ar.getChangedAt() + " [" + ar.getField() + "] " + ar.getOldValue() + " -> " + ar.getNewValue() + " (" + ar.getReason() + ")");
        }
    }

    private void deleteClient() throws SQLException {
        long id = readLong("ID client: ");
        clientRepository.delete(id);
        System.out.println("Client supprime.");
    }

    private void listClients() throws SQLException {

        List<Personne> list = clientRepository.findAll();
        for (Personne p : list) {
            System.out.println(p.getId() + " - " + p.getNom() + " " + p.getPrenom() + " - score=" + p.getScore());
        }
    }

    private void requestCredit() throws SQLException {
        long id = readLong("ID client: ");
        Optional<Personne> opt = clientRepository.findById(id);
        if (!opt.isPresent()) { System.out.println("Introuvable."); return; }
        Personne p = opt.get();
        boolean isExisting = readYesNo("Client existant ? (y/n): ");
        double income = (p instanceof Employe) ? ((Employe) p).getSalaire() : ((Professionnel) p).getRevenu();
        int score = new ScoringService(auditRepository).computeScore(p, Collections.emptyList(), isExisting);
        clientRepository.updateScore(p.getId(), score);
        double requested = readDouble("Montant demande (DH): ");
        int months = readInt("Duree (mois): ");
        double annualRate = readDouble("Taux interet annuel (ex 0.12 pour 12%): ");
        System.out.print("Type credit (conso/habitat/...): ");
        String typeCredit = scanner.nextLine().trim();
        Credit cr = creditService.requestCredit(p, isExisting, score, income, requested, months, annualRate, typeCredit);
        System.out.println("Decision: " + cr.getDecision() + " | Montant octroye: " + cr.getMontantOctroye());
    }

    private void simulatePayment() throws SQLException {
        long creditId = readLong("ID credit: ");
        List<Echeance> echs = echeanceRepository.findByCredit(creditId);
        if (echs.isEmpty()) { System.out.println("Aucune echeance."); return; }
        for (Echeance e : echs) {
            System.out.println(e.getId() + " - due " + DateUtils.format(e.getDateEcheance()) + " - statut " + e.getStatutPaiement() + " - montant " + e.getMensualite());
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

    private void runAnalytics() throws SQLException {
        System.out.println("1) Recherche filtree");
        System.out.println("2) Top-10 clients risques");
        System.out.println("3) Moyenne score par secteur");
        System.out.println("4) Selection campagne");
        String ch = scanner.nextLine().trim();
        switch (ch) {
            case "1": {
                int minAge = readInt("Age min: ");
                int maxAge = readInt("Age max: ");
                double minInc = readDouble("Revenu min: ");
                double maxInc = readDouble("Revenu max: ");
                String sf = readString("Situation familiale (Marie/Celibataire ou vide): ");
                if (ValidationUtils.isNullOrEmpty(sf)) sf = null;
                List<Personne> res = analyticsService.searchByAgeIncomeAndMarital(minAge, maxAge, minInc, maxInc, sf);
                res.forEach(p -> System.out.println(p.getId() + " - " + p.getNom() + " " + p.getPrenom() + " score=" + p.getScore()));
                break;
            }
            case "2": {
                analyticsService.top10RiskyClients().forEach(p -> System.out.println(p.getId() + " - " + p.getNom() + " " + p.getPrenom() + " score=" + p.getScore()));
                break;
            }
            case "3": {
                analyticsService.averageScorePerSector().forEach((k, v) -> System.out.println(k + " -> " + String.format("%.2f", v)));
                break;
            }
            case "4": {
                analyticsService.campaignSelection().forEach(p -> System.out.println(p.getId() + " - " + p.getNom() + " " + p.getPrenom()));
                break;
            }
            default: System.out.println("Choix invalide.");
        }
    }

    private void runDemo() throws SQLException {
        System.out.println("Demo: lister clients seed, calculer score, demander un credit, generer echeances.");
        listClients();
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
        for (Echeance e : new EcheanceRepository().findByCredit(cr.getId())) {
            System.out.println(" - " + e.getId() + " " + e.getDateEcheance() + " " + e.getMensualite() + " " + e.getStatutPaiement());
        }
    }

    private void fillBase(Personne p) {
        System.out.print("Nom: "); p.setNom(scanner.nextLine().trim());
        System.out.print("Prenom: "); p.setPrenom(scanner.nextLine().trim());
        p.setDateNaissance(DateUtils.parse(readString("Date naissance (YYYY-MM-DD): ")));
        System.out.print("Ville: "); p.setVille(scanner.nextLine().trim());
        p.setNombreEnfants(readInt("Nombre d'enfants: "));
        p.setInvestissement(readYesNo("Investissements/placements ? (y/n): "));
        p.setPlacement(p.isInvestissement());
        System.out.print("Situation familiale (Marie/Celibataire): ");
        p.setSituationFamiliale(scanner.nextLine().trim());
        p.setCreatedAt(java.time.LocalDateTime.now());
    }

    private TypeContrat readTypeContrat() {
        System.out.println("Type contrat: 1)CDI_PUBLIC 2)CDI_PRIVE_GRANDE_ENTREPRISE 3)CDI_PRIVE_PME 4)CDD_INTERIM");
        String s = scanner.nextLine().trim();
        switch (s) {
            case "1": return TypeContrat.CDI_PUBLIC;
            case "2": return TypeContrat.CDI_PRIVE_GRANDE_ENTREPRISE;
            case "3": return TypeContrat.CDI_PRIVE_PME;
            default: return TypeContrat.CDD_INTERIM;
        }
    }

    private Secteur readSecteur() {
        System.out.println("Secteur: 1)PUBLIC \n 2)PRIVE_GRANDE_ENTREPRISE \n3)PRIVE_PME \n4)PROFESSION_LIBERALE \n5)AUTO_ENTREPRENEUR");
        String s = scanner.nextLine().trim();
        switch (s) {
            case "1": return Secteur.PUBLIC;
            case "2": return Secteur.PRIVE_GRANDE_ENTREPRISE;
            case "3": return Secteur.PRIVE_PME;
            case "4": return Secteur.PROFESSION_LIBERALE;
            default: return Secteur.AUTO_ENTREPRENEUR;
        }
    }

    private String readString(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }

    private int readInt(String label) {
        while (true) {
            try {
                System.out.print(label);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Nombre invalide, reessayez.");
            }
        }
    }

    private long readLong(String label) {
        while (true) {
            try {
                System.out.print(label);
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Nombre invalide, reessayez.");
            }
        }
    }

    private double readDouble(String label) {
        while (true) {
            try {
                System.out.print(label);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Nombre invalide, reessayez.");
            }
        }
    }

    private boolean readYesNo(String label) {
        System.out.print(label);
        String s = scanner.nextLine().trim().toLowerCase();
        return s.startsWith("y") || s.equals("oui") || s.equals("o");
    }
}
