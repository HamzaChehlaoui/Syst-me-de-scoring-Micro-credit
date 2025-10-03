package repository;


import enums.PaymentStatus;
import model.Echeance;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.sql.Date;
import java.util.*;

public class EcheanceRepository {

    public void createAll(List<Echeance> list) throws SQLException {
        String sql = "INSERT INTO echeance(credit_id,date_echeance,mensualite,date_paiement,statut_paiement) VALUES (?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (Echeance e : list) {
                ps.setLong(1, e.getCreditId());
                ps.setDate(2, Date.valueOf(e.getDateEcheance()));
                ps.setBigDecimal(3, java.math.BigDecimal.valueOf(e.getMensualite()));
                if (e.getDatePaiement() == null) ps.setNull(4, Types.DATE); else ps.setDate(4, Date.valueOf(e.getDatePaiement()));
                ps.setString(5, e.getStatutPaiement().name());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public List<Echeance> findByCredit(long creditId) throws SQLException {
        List<Echeance> out = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM echeance WHERE credit_id=? ORDER BY date_echeance ASC")) {
            ps.setLong(1, creditId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    public Optional<Echeance> findById(long id) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM echeance WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(map(rs));
            }
        }
    }

    public void updatePayment(long id, LocalDate datePaiement, PaymentStatus status) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE echeance SET date_paiement=?, statut_paiement=? WHERE id=?")) {
            if (datePaiement == null) ps.setNull(1, Types.DATE); else ps.setDate(1, Date.valueOf(datePaiement));
            ps.setString(2, status.name());
            ps.setLong(3, id);
            ps.executeUpdate();
        }
    }

    private Echeance map(ResultSet rs) throws SQLException {
        Echeance e = new Echeance();
        e.setId(rs.getLong("id"));
        e.setCreditId(rs.getLong("credit_id"));
        e.setDateEcheance(rs.getDate("date_echeance").toLocalDate());
        e.setMensualite(rs.getBigDecimal("mensualite").doubleValue());
        Date dp = rs.getDate("date_paiement");
        e.setDatePaiement(dp == null ? null : dp.toLocalDate());
        e.setStatutPaiement(PaymentStatus.valueOf(rs.getString("statut_paiement")));
        return e;
    }
}
