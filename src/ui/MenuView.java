package ui;

import java.util.Scanner;

public class MenuView {
    private final Scanner scanner;

    public MenuView(Scanner scanner) {
        this.scanner = scanner;
    }

    public String displayMainMenu() {
        System.out.println("\n=== Microfinance CLI ===");
        System.out.println("1. Create client (Employe/Professionnel)");
        System.out.println("2. Edit client");
        System.out.println("3. View client profile (score & audit)");
        System.out.println("4. Delete client");
        System.out.println("5. List all clients");
        System.out.println("6. Request credit");
        System.out.println("7. Simulate payment for an echeance");
        System.out.println("8. Run analytics reports");
        System.out.println("9. Run demo scenario");
        System.out.println("0. Exit");
        System.out.print("Votre choix: ");
        return scanner.nextLine().trim();
    }
}
