package model;

import java.time.LocalDate;
import enums.PaymentStatus;

/**
 * Echeance de remboursement.
 */
public class Echeance {
    private Long id;
    private Long creditId;
    private LocalDate dateEcheance;
    private double mensualite;
    private LocalDate datePaiement; // nullable
    private PaymentStatus statutPaiement;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCreditId() { return creditId; }
    public void setCreditId(Long creditId) { this.creditId = creditId; }
    public LocalDate getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(LocalDate dateEcheance) { this.dateEcheance = dateEcheance; }
    public double getMensualite() { return mensualite; }
    public void setMensualite(double mensualite) { this.mensualite = mensualite; }
    public LocalDate getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDate datePaiement) { this.datePaiement = datePaiement; }
    public PaymentStatus getStatutPaiement() { return statutPaiement; }
    public void setStatutPaiement(PaymentStatus statutPaiement) { this.statutPaiement = statutPaiement; }
}
