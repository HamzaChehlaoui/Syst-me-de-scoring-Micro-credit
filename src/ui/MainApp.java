
import view.AnalyticsView;
import view.ClientView;
import view.CreditView;
import view.PaymentView;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private Scanner scanner = new Scanner(System.in);

    public void start() throws SQLException {
        int choix;
        do {
            System.out.println("=== MENU PRINCIPAL ===");
            System.out.println("1. Gestion des Clients");
            System.out.println("2. Gestion des Crédits");
            System.out.println("3. Paiements & Incidents");
            System.out.println("4. Analytics / Statistiques");
            System.out.println("0. Quitter");
            System.out.print("Choix: ");
            choix = scanner.nextInt();

            switch (choix) {
                case 1:
                    new ClientView().menu();
                    break;
                case 2:
                    new CreditView().menu();
                    break;
                case 3:
                    new PaymentView().menu();
                    break;
                case 4:
                    new AnalyticsView().menu();
                    break;
                case 0:
                    System.out.println("Au revoir !");
                    break;
                default:
                    System.out.println("Choix invalide !");
            }
        } while (choix != 0);
    }

    public static void main(String[] args) throws SQLException {
        new Main().start();
    }
}
