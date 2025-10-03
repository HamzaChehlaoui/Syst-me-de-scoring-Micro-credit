package service;


import enums.DecisionType;
import model.Credit;
import model.Echeance;
import model.Personne;
import repository.CreditRepository;
import repository.EcheanceRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class CreditService {

    private final CreditRepository creditRepository;
    private final EcheanceRepository echeanceRepository;

    public CreditService(CreditRepository creditRepository, EcheanceRepository echeanceRepository) {
        this.creditRepository = creditRepository;
        this.echeanceRepository = echeanceRepository;
    }


    public Credit requestCredit(Personne p, boolean isExisting, int score, double monthlyIncome, double amountRequested, int months, double annualRate, String typeCredit) throws SQLException {
        Credit cr = new Credit();
        cr.setPersonneId(p.getId());
        cr.setDateCredit(LocalDate.now());
        cr.setMontantDemande(amountRequested);
        cr.setDureeMois(months);
        cr.setTauxInteret(annualRate);
        cr.setTypeCredit(typeCredit);
        DecisionType decision = decide(isExisting, score);
        cr.setDecision(decision.name());
        cr = creditRepository.create(cr);

        double maxAmount = maxLoan(isExisting, score, monthlyIncome);
        Double granted = null;
        if (isEligible(isExisting, score)) {
            granted = Math.min(amountRequested, maxAmount);
            creditRepository.updateDecisionAndAmount(cr.getId(), decision.name(), granted);
            cr.setMontantOctroye(granted);
            List<Echeance> schedule = generateSchedule(cr.getId(), granted, annualRate, months, LocalDate.now().plusMonths(1));
            echeanceRepository.createAll(schedule);
        } else {
            creditRepository.updateDecisionAndAmount(cr.getId(), decision.name(), null);
        }
        return cr;
    }

    private DecisionType decide(boolean isExisting, int score) {
        if (score >= 80) return DecisionType.ACCORD_IMMEDIAT;
        if (!isExisting) {
            if (score >= 60) return DecisionType.ETUDE_MANUELLE;
            return DecisionType.REFUS_AUTOMATIQUE;
        } else {
            if (score >= 50) return DecisionType.ETUDE_MANUELLE;
            return DecisionType.REFUS_AUTOMATIQUE;
        }
    }

    private boolean isEligible(boolean isExisting, int score) {
        return (!isExisting && score >= 70) || (isExisting && score >= 60);
    }

    private double maxLoan(boolean isExisting, int score, double salary) {
        if (!isExisting) return 4.0 * salary;
        if (score > 80) return 10.0 * salary;
        if (score >= 60) return 7.0 * salary;
        return 0.0;
    }

    /**
     * Annuity formula: M = P * r / (1 - (1+r)^-n), where r is monthly rate
     */
    private List<Echeance> generateSchedule(long creditId, double principal, double annualRate, int months, LocalDate firstDueDate) {
        double monthlyRate = annualRate / 12.0;
        double mensualite = (monthlyRate == 0)
                ? (principal / months)
                : principal * monthlyRate / (1 - Math.pow(1 + monthlyRate, -months));

        List<Echeance> list = new ArrayList<>();
        for (int i = 0; i < months; i++) {
            Echeance e = new Echeance();
            e.setCreditId(creditId);
            e.setDateEcheance(firstDueDate.plusMonths(i));
            e.setMensualite(Math.round(mensualite * 100.0) / 100.0);
            e.setStatutPaiement(enums.PaymentStatus.A_PAYER);
            list.add(e);
        }
        return list;
    }
}
