package ui;

import model.*;
import repository.*;
import enums.*;
import utils.DateUtils;
import utils.ValidationUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Scanner;

public class ClientView {
    private final Scanner scanner;
    private final ClientRepository clientRepository;
    private final AuditRepository auditRepository;

    public ClientView(Scanner scanner, ClientRepository clientRepository, AuditRepository auditRepository) {
        this.scanner = scanner;
        this.clientRepository = clientRepository;
        this.auditRepository = auditRepository;
    }
    private String readClientType() {
        while (true) {
            System.out.print("Type (1=Employe, 2=Professionnel): ");
            String input = scanner.nextLine().trim();
            if (input.equals("1") || input.equals("2")) {
                return input;
            }
            System.out.println("Veuillez entrer 1 ou 2 seulement.");
        }
    }

    public void createClient() throws SQLException {
        System.out.print("Type (1=Employe, 2=Professionnel): ");
        String type = readClientType();
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

    public void editClient() throws SQLException {
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

    public void viewClient() throws SQLException {
        long id = readLong("ID client: ");
        Optional<Personne> opt = clientRepository.findById(id);
        if (!opt.isPresent()) { System.out.println("Introuvable."); return; }
        Personne p = opt.get();
        System.out.println("Client: " + p.getNom() + " " + p.getPrenom() + " (" + p.getType() + ")");
        System.out.println("Score: " + p.getScore());
        System.out.println("Audit:");
        for (AuditRecord ar : auditRepository.findByEntity("Personne", p.getId())) {
            System.out.println(" - " + ar.getChangedAt() + " [" + ar.getField() + "] "
                    + ar.getOldValue() + " -> " + ar.getNewValue() + " (" + ar.getReason() + ")");
        }
    }

    public void deleteClient() throws SQLException {
        long id = readLong("ID client: ");
        clientRepository.delete(id);
        System.out.println("Client supprime.");
    }

    public void listClients() throws SQLException {
        List<Personne> list = clientRepository.findAll();
        for (Personne p : list) {
            System.out.println(p.getId() + " - " + p.getNom() + " " + p.getPrenom() + " - score=" + p.getScore());
        }
    }

    // -------- Helpers --------
    private void fillBase(Personne p) {
        p.setNom(readNonEmptyString("Nom: "));
        p.setPrenom(readNonEmptyString("Prenom: "));
        p.setDateNaissance(DateUtils.parse(readNonEmptyString("Date naissance (YYYY-MM-DD): ")));
        p.setVille(readNonEmptyString("Ville: "));
        p.setNombreEnfants(readInt("Nombre d'enfants: "));
        p.setInvestissement(readYesNo("Investissements/placements ? (y/n): "));
        p.setPlacement(p.isInvestissement());
        p.setSituationFamiliale(readNonEmptyString("Situation familiale (Marie/Celibataire): "));
        p.setCreatedAt(LocalDateTime.now());
    }
    private String readNonEmptyString(String label) {
        while (true) {
            System.out.print(label);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Ce champ ne peut pas être vide. Réessayez.");
        }
    }

    private TypeContrat readTypeContrat() {
        while (true) {
            System.out.println("Type contrat: ");
            System.out.println(" 1) CDI_PUBLIC");
            System.out.println(" 2) CDI_PRIVE_GRANDE_ENTREPRISE");
            System.out.println(" 3) CDI_PRIVE_PME");
            System.out.println(" 4) CDD_INTERIM");

            String s = scanner.nextLine().trim();
            switch (s) {
                case "1": return TypeContrat.CDI_PUBLIC;
                case "2": return TypeContrat.CDI_PRIVE_GRANDE_ENTREPRISE;
                case "3": return TypeContrat.CDI_PRIVE_PME;
                case "4": return TypeContrat.CDD_INTERIM;
                default:
                    System.out.println("Choix invalide, veuillez réessayer.");
            }
        }
    }

    private Secteur readSecteur() {
        while (true) {
            System.out.println("Secteur: ");
            System.out.println(" 1) PUBLIC");
            System.out.println(" 2) PRIVE_GRANDE_ENTREPRISE");
            System.out.println(" 3) PRIVE_PME");
            System.out.println(" 4) PROFESSION_LIBERALE");
            System.out.println(" 5) AUTO_ENTREPRENEUR");

            String s = scanner.nextLine().trim();
            switch (s) {
                case "1": return Secteur.PUBLIC;
                case "2": return Secteur.PRIVE_GRANDE_ENTREPRISE;
                case "3": return Secteur.PRIVE_PME;
                case "4": return Secteur.PROFESSION_LIBERALE;
                case "5": return Secteur.AUTO_ENTREPRENEUR;
                default:
                    System.out.println("Choix invalide, veuillez réessayer.");
            }
        }
    }

    private String readString(String label) {
        while (true) {
            System.out.print(label);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Ce champ ne peut pas être vide. Réessayez.");
        }
    }

    private int readInt(String label) {
        while (true) {
            System.out.print(label);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Nombre invalide, réessayez (ex: 5).");
            }
        }
    }

    private long readLong(String label) {
        while (true) {
            System.out.print(label);
            String input = scanner.nextLine().trim();
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Nombre invalide, réessayez (ex: 1001).");
            }
        }
    }

    private double readDouble(String label) {
        while (true) {
            System.out.print(label);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Nombre invalide, réessayez (ex: 1234.56).");
            }
        }
    }

    private boolean readYesNo(String label) {
        while (true) {
            System.out.print(label);
            String s = scanner.nextLine().trim().toLowerCase();
            if (s.startsWith("y") || s.equals("oui") || s.equals("o")) {
                return true;
            } else if (s.startsWith("n") || s.equals("non")) {
                return false;
            }
            System.out.println("Réponse invalide. Tapez (y/n ou oui/non).");
        }
    }

}
