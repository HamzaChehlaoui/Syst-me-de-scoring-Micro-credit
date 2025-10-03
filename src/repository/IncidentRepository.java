package repository;



import enums.IncidentType;
import model.Incident;
import utils.DatabaseConnection;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class IncidentRepository {

    public Incident create(Incident inc) throws SQLException {
        String sql = "INSERT INTO incident(date_incident,echeance_id,type_incident,impact_score,note) VALUES (?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(inc.getDateIncident()));
            ps.setLong(2, inc.getEcheanceId());
            ps.setString(3, inc.getTypeIncident().name());
            ps.setInt(4, inc.getImpactScore());
            ps.setString(5, inc.getNote());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) inc.setId(keys.getLong(1));
            }
        }
        return inc;
    }

    public List<Incident> findRecentMonths(int months) throws SQLException {
        String sql = "SELECT * FROM incident WHERE date_incident >= DATE_SUB(CURDATE(), INTERVAL ? MONTH)";
        List<Incident> out = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, months);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    public List<Incident> findByEcheance(long echeanceId) throws SQLException {
        List<Incident> out = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM incident WHERE echeance_id=?")) {
            ps.setLong(1, echeanceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    private Incident map(ResultSet rs) throws SQLException {
        Incident i = new Incident();
        i.setId(rs.getLong("id"));
        i.setDateIncident(rs.getDate("date_incident").toLocalDate());
        i.setEcheanceId(rs.getLong("echeance_id"));
        i.setTypeIncident(IncidentType.valueOf(rs.getString("type_incident")));
        i.setImpactScore(rs.getInt("impact_score"));
        i.setNote(rs.getString("note"));
        return i;
    }
}
