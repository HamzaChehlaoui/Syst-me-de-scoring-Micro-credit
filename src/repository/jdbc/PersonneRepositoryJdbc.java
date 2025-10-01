package repository;

import model.Personne;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonneRepositoryJdbc extends GenericRepositoryJdbc<Personne, Long> implements PersonneRepository {

    public PersonneRepositoryJdbc() throws SQLException {
        super();
    }

    @Override
    public Personne save(Personne p) throws SQLException {
        String sql = "INSERT INTO personne (nom, prenom, date_naissance, ville, nombre_enfants, investissement, placement, situation_familiale, created_at, score, type_personne) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, p.getNom());
            stmt.setString(2, p.getPrenom());
            stmt.setDate(3, Date.valueOf(p.getDateNaissance()));
            stmt.setString(4, p.getVille());
            stmt.setInt(5, p.getNombreEnfants());
            stmt.setDouble(6, p.getInvestissement());
            stmt.setDouble(7, p.getPlacement());
            stmt.setString(8, p.getSituationFamiliale().name());
            stmt.setDate(9, Date.valueOf(LocalDate.now()));
            stmt.setInt(10, p.getScore());
            stmt.setString(11, p.getTypePersonne().name());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                p.setId(rs.getLong(1));
            }
        }
        return p;
    }

    @Override
    public Optional<Personne> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM personne WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Personne p = mapResultSetToPersonne(rs);
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Personne> findAll() throws SQLException {
        String sql = "SELECT * FROM personne";
        List<Personne> personnes = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                personnes.add(mapResultSetToPersonne(rs));
            }
        }
        return personnes;
    }

    @Override
    public void update(Personne p) throws SQLException {
        String sql = "UPDATE personne SET nom=?, prenom=?, date_naissance=?, ville=?, nombre_enfants=?, investissement=?, placement=?, situation_familiale=?, score=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, p.getNom());
            stmt.setString(2, p.getPrenom());
            stmt.setDate(3, Date.valueOf(p.getDateNaissance()));
            stmt.setString(4, p.getVille());
            stmt.setInt(5, p.getNombreEnfants());
            stmt.setDouble(6, p.getInvestissement());
            stmt.setDouble(7, p.getPlacement());
            stmt.setString(8, p.getSituationFamiliale().name());
            stmt.setInt(9, p.getScore());
            stmt.setLong(10, p.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM personne WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Personne mapResultSetToPersonne(ResultSet rs) throws SQLException {
        Personne p = new Personne() {}; // abstract, pour simplifier
        p.setId(rs.getLong("id"));
        p.setNom(rs.getString("nom"));
        p.setPrenom(rs.getString("prenom"));
        p.setDateNaissance(rs.getDate("date_naissance").toLocalDate());
        p.setVille(rs.getString("ville"));
        p.setNombreEnfants(rs.getInt("nombre_enfants"));
        p.setInvestissement(rs.getDouble("investissement"));
        p.setPlacement(rs.getDouble("placement"));
        p.setSituationFamiliale(Enum.valueOf(model.enums.SituationFamiliale.class, rs.getString("situation_familiale")));
        p.setScore(rs.getInt("score"));
        p.setTypePersonne(Enum.valueOf(model.enums.TypePersonne.class, rs.getString("type_personne")));
        return p;
    }
}
