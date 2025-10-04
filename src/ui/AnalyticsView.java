package ui;

import model.*;
import repository.*;
import service.*;
import utils.ValidationUtils;

import java.sql.SQLException;
import java.util.*;

public class AnalyticsView {
    private final Scanner scanner;
    private final AnalyticsService analyticsService;

    public AnalyticsView(Scanner scanner, AnalyticsService analyticsService) {
        this.scanner = scanner;
        this.analyticsService = analyticsService;
    }

    public void runAnalytics() throws SQLException {
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

    private int readInt(String label) { System.out.print(label); return Integer.parseInt(scanner.nextLine().trim()); }
    private double readDouble(String label) { System.out.print(label); return Double.parseDouble(scanner.nextLine().trim()); }
    private String readString(String label) { System.out.print(label); return scanner.nextLine().trim(); }
}
