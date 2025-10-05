package ui;

import repository.*;
import service.*;

import java.util.Scanner;

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
    private final AnalyticsService analyticsService = new AnalyticsService(clientRepository, creditRepository, incidentRepository ,echeanceRepository);

    private final MenuView menuView = new MenuView(scanner);
    private final ClientView clientView = new ClientView(scanner, clientRepository, auditRepository);
    private final CreditView creditView = new CreditView(scanner, clientRepository, creditService, auditRepository, scoringService);
    private final PaymentView paymentView = new PaymentView(scanner, echeanceRepository, paymentService, clientRepository ,creditRepository , incidentRepository);
    private final AnalyticsView analyticsView = new AnalyticsView(scanner, analyticsService);
    private final DemoView demoView = new DemoView(scanner, clientRepository, creditService, scoringService, echeanceRepository);

    public static void main(String[] args) {
        new MainApp().run();
    }

    private void run() {
        while (true) {
            String choice = menuView.displayMainMenu();
            try {
                switch (choice) {
                    case "1": clientView.createClient(); break;
                    case "2": clientView.editClient(); break;
                    case "3": clientView.viewClient(); break;
                    case "4": clientView.deleteClient(); break;
                    case "5": clientView.listClients(); break;
                    case "6": creditView.requestCredit(); break;
                    case "7": paymentView.simulatePayment(); break;
                    case "8": analyticsView.runAnalytics(); break;
                    case "9": demoView.runDemo(); break;
                    case "0": System.out.println("Au revoir!"); return;
                    default: System.out.println("Choix invalide.");
                }
            } catch (Exception ex) {
                System.out.println("Erreur: " + ex.getMessage());
            }
        }
    }
}
