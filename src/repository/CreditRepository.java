package repository;



import model.Credit;
import utils.DatabaseConnection;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;

public class CreditRepository {

    public Credit create(Credit cr) throws SQLException {
        String sql = "INSERT INTO credit(personne_id,date_credit,montant_demande,montant_octroye,taux_interet,duree_mois,type_credit,decision,created_at) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, cr.getPersonneId());
            ps.setDate(2, Date.valueOf(cr.getDateCredit()));
            ps.setBigDecimal(3, java.math.BigDecimal.valueOf(cr.getMontantDemande()));
            if (cr.getMontantOctroye() == null) ps.setNull(4, Types.DECIMAL); else ps.setBigDecimal(4, java.math.BigDecimal.valueOf(cr.getMontantOctroye()));
            ps.setBigDecimal(5, java.math.BigDecimal.valueOf(cr.getTauxInteret()));
            ps.setInt(6, cr.getDureeMois());
            ps.setString(7, cr.getTypeCredit());
            ps.setString(8, cr.getDecision());
            ps.setTimestamp(9, Timestamp.valueOf(cr.getCreatedAt() == null ? LocalDateTime.now() : cr.getCreatedAt()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) cr.setId(keys.getLong(1));
            }
        }
        return cr;
    }

    public Optional<Credit> findById(long id) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM credit WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(map(rs));
            }
        }
    }

    public List<Credit> findByPersonne(long personneId) throws SQLException {
        List<Credit> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM credit WHERE personne_id=? ORDER BY created_at DESC")) {
            ps.setLong(1, personneId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private Credit map(ResultSet rs) throws SQLException {
        Credit cr = new Credit();
        cr.setId(rs.getLong("id"));
        cr.setPersonneId(rs.getLong("personne_id"));
        cr.setDateCredit(rs.getDate("date_credit").toLocalDate());
        cr.setMontantDemande(rs.getBigDecimal("montant_demande").doubleValue());
        double mo = rs.getDouble("montant_octroye");
        cr.setMontantOctroye(rs.wasNull() ? null : mo);
        cr.setTauxInteret(rs.getBigDecimal("taux_interet").doubleValue());
        cr.setDureeMois(rs.getInt("duree_mois"));
        cr.setTypeCredit(rs.getString("type_credit"));
        cr.setDecision(rs.getString("decision"));
        Timestamp ts = rs.getTimestamp("created_at");
        cr.setCreatedAt(ts == null ? LocalDateTime.now() : ts.toLocalDateTime());
        return cr;
    }

    public void updateDecisionAndAmount(long id, String decision, Double montantOctroye) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE credit SET decision=?, montant_octroye=? WHERE id=?")) {
            ps.setString(1, decision);
            if (montantOctroye == null) ps.setNull(2, Types.DECIMAL);
            else ps.setBigDecimal(2, java.math.BigDecimal.valueOf(montantOctroye));
            ps.setLong(3, id);
            ps.executeUpdate();
        }
    }
}