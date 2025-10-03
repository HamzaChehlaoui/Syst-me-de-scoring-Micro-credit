package repository;



import model.*;
import enums.*;
import service.ScoringService;
import utils.DatabaseConnection;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;

/**
 * JDBC repository for Personne (Employe / Professionnel).
 * Implements CRUD and mapping.
 */
public class ClientRepository {

    // In ClientRepository.create() method, after the transaction commits:
    public Personne create(Personne p) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try {
                Long id = insertPersonne(c, p);
                p.setId(id);
                if (p instanceof Employe) {
                    insertEmploye(c, (Employe) p);
                } else if (p instanceof Professionnel) {
                    insertProfessionnel(c, (Professionnel) p);
                }
                c.commit();

                // ADD THIS: Calculate and persist score after creation
                ScoringService scoringService = new ScoringService(new AuditRepository());
                int score = scoringService.computeScore(p, Collections.emptyList(), false); // false = new client
                updateScore(p.getId(), score);

                return p;
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public Optional<Personne> findById(long id) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection()) {
            String sql = "SELECT * FROM personne WHERE id=?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return Optional.empty();
                    String type = rs.getString("type");
                    if ("EMPLOYE".equalsIgnoreCase(type)) {
                        return Optional.of(loadEmploye(c, rs));
                    } else {
                        return Optional.of(loadProfessionnel(c, rs));
                    }
                }
            }
        }
    }

    public List<Personne> findAll() throws SQLException {
        List<Personne> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection()) {
            String sql = "SELECT * FROM personne ORDER BY id";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String type = rs.getString("type");
                        if ("EMPLOYE".equalsIgnoreCase(type)) {
                            list.add(loadEmploye(c, rs));
                        } else {
                            list.add(loadProfessionnel(c, rs));
                        }
                    }
                }
            }
        }
        return list;
    }


    public void update(Personne p) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try {
                updatePersonne(c, p);
                if (p instanceof Employe) {
                    updateEmploye(c, (Employe) p);
                } else if (p instanceof Professionnel) {
                    updateProfessionnel(c, (Professionnel) p);
                }
                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM employe WHERE personne_id=?")) {
                    ps.setLong(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM professionnel WHERE personne_id=?")) {
                    ps.setLong(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM personne WHERE id=?")) {
                    ps.setLong(1, id);
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public void updateScore(long personneId, int score) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE personne SET score=? WHERE id=?")) {
            ps.setInt(1, score);
            ps.setLong(2, personneId);
            ps.executeUpdate();
        }
    }

    private Long insertPersonne(Connection c, Personne p) throws SQLException {
        String sql = "INSERT INTO personne(nom,prenom,date_naissance,ville,nombre_enfants,investissement,placement,situation_familiale,created_at,score,type) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setDate(3, Date.valueOf(p.getDateNaissance()));
            ps.setString(4, p.getVille());
            ps.setInt(5, p.getNombreEnfants());
            ps.setBoolean(6, p.isInvestissement());
            ps.setBoolean(7, p.isPlacement());
            ps.setString(8, p.getSituationFamiliale());
            LocalDateTime ts = p.getCreatedAt() == null ? LocalDateTime.now() : p.getCreatedAt();
            ps.setTimestamp(9, Timestamp.valueOf(ts));
            if (p.getScore() == null) ps.setNull(10, Types.INTEGER); else ps.setInt(10, p.getScore());
            ps.setString(11, p.getType());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("Failed to insert personne");
    }

    private void insertEmploye(Connection c, Employe e) throws SQLException {
        String sql = "INSERT INTO employe(personne_id,salaire,anciennete_years,poste,type_contrat,secteur) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, e.getId());
            ps.setBigDecimal(2, java.math.BigDecimal.valueOf(e.getSalaire()));
            ps.setInt(3, e.getAncienneteYears());
            ps.setString(4, e.getPoste());
            ps.setString(5, e.getTypeContrat() == null ? null : e.getTypeContrat().name());
            ps.setString(6, e.getSecteur() == null ? null : e.getSecteur().name());
            ps.executeUpdate();
        }
    }

    private void insertProfessionnel(Connection c, Professionnel p) throws SQLException {
        String sql = "INSERT INTO professionnel(personne_id,revenu,immatriculation_fiscale,secteur_activite,activite) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, p.getId());
            ps.setBigDecimal(2, java.math.BigDecimal.valueOf(p.getRevenu()));
            ps.setString(3, p.getImmatriculationFiscale());
            ps.setString(4, p.getSecteurActivite());
            ps.setString(5, p.getActivite());
            ps.executeUpdate();
        }
    }

    private void updatePersonne(Connection c, Personne p) throws SQLException {
        String sql = "UPDATE personne SET nom=?, prenom=?, date_naissance=?, ville=?, nombre_enfants=?, investissement=?, placement=?, situation_familiale=?, score=?, type=? WHERE id=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setDate(3, Date.valueOf(p.getDateNaissance()));
            ps.setString(4, p.getVille());
            ps.setInt(5, p.getNombreEnfants());
            ps.setBoolean(6, p.isInvestissement());
            ps.setBoolean(7, p.isPlacement());
            ps.setString(8, p.getSituationFamiliale());
            if (p.getScore() == null) ps.setNull(9, Types.INTEGER); else ps.setInt(9, p.getScore());
            ps.setString(10, p.getType());
            ps.setLong(11, p.getId());
            ps.executeUpdate();
        }
    }

    private void updateEmploye(Connection c, Employe e) throws SQLException {
        String sql = "UPDATE employe SET salaire=?, anciennete_years=?, poste=?, type_contrat=?, secteur=? WHERE personne_id=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, java.math.BigDecimal.valueOf(e.getSalaire()));
            ps.setInt(2, e.getAncienneteYears());
            ps.setString(3, e.getPoste());
            ps.setString(4, e.getTypeContrat() == null ? null : e.getTypeContrat().name());
            ps.setString(5, e.getSecteur() == null ? null : e.getSecteur().name());
            ps.setLong(6, e.getId());
            int updated = ps.executeUpdate();
            if (updated == 0) insertEmploye(c, e);
        }
    }

    private void updateProfessionnel(Connection c, Professionnel p) throws SQLException {
        String sql = "UPDATE professionnel SET revenu=?, immatriculation_fiscale=?, secteur_activite=?, activite=? WHERE personne_id=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, java.math.BigDecimal.valueOf(p.getRevenu()));
            ps.setString(2, p.getImmatriculationFiscale());
            ps.setString(3, p.getSecteurActivite());
            ps.setString(4, p.getActivite());
            ps.setLong(5, p.getId());
            int updated = ps.executeUpdate();
            if (updated == 0) insertProfessionnel(c, p);
        }
    }

    private Employe loadEmploye(Connection c, ResultSet baseRs) throws SQLException {
        Employe e = new Employe();
        fillBase(e, baseRs);
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM employe WHERE personne_id=?")) {
            ps.setLong(1, e.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    e.setSalaire(rs.getBigDecimal("salaire").doubleValue());
                    e.setAncienneteYears(rs.getInt("anciennete_years"));
                    e.setPoste(rs.getString("poste"));
                    String contrat = rs.getString("type_contrat");
                    String secteur = rs.getString("secteur");
                    if (contrat != null) e.setTypeContrat(enums.TypeContrat.valueOf(contrat));
                    if (secteur != null) e.setSecteur(enums.Secteur.valueOf(secteur));
                }
            }
        }
        return e;
    }

    private Professionnel loadProfessionnel(Connection c, ResultSet baseRs) throws SQLException {
        Professionnel p = new Professionnel();
        fillBase(p, baseRs);
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM professionnel WHERE personne_id=?")) {
            ps.setLong(1, p.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p.setRevenu(rs.getBigDecimal("revenu").doubleValue());
                    p.setImmatriculationFiscale(rs.getString("immatriculation_fiscale"));
                    p.setSecteurActivite(rs.getString("secteur_activite"));
                    p.setActivite(rs.getString("activite"));
                }
            }
        }
        return p;
    }

    private void fillBase(Personne p, ResultSet rs) throws SQLException {
        p.setId(rs.getLong("id"));
        p.setNom(rs.getString("nom"));
        p.setPrenom(rs.getString("prenom"));
        Date dn = rs.getDate("date_naissance");
        p.setDateNaissance(dn.toLocalDate());
        p.setVille(rs.getString("ville"));
        p.setNombreEnfants(rs.getInt("nombre_enfants"));
        p.setInvestissement(rs.getBoolean("investissement"));
        p.setPlacement(rs.getBoolean("placement"));
        p.setSituationFamiliale(rs.getString("situation_familiale"));
        Timestamp created = rs.getTimestamp("created_at");
        p.setCreatedAt(created == null ? LocalDateTime.now() : created.toLocalDateTime());
        int sc = rs.getInt("score");
        p.setScore(rs.wasNull() ? null : sc);
        p.setType(rs.getString("type"));
    }
}