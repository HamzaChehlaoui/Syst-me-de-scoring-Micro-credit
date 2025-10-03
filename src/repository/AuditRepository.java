package repository;


import model.AuditRecord;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditRepository {

    public void save(AuditRecord ar) throws SQLException {
        String sql = "INSERT INTO audit(entity_type,entity_id,field,old_value,new_value,changed_at,reason) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, ar.getEntityType());
            ps.setLong(2, ar.getEntityId());
            ps.setString(3, ar.getField());
            ps.setString(4, ar.getOldValue());
            ps.setString(5, ar.getNewValue());
            ps.setTimestamp(6, Timestamp.valueOf(ar.getChangedAt() == null ? LocalDateTime.now() : ar.getChangedAt()));
            ps.setString(7, ar.getReason());
            ps.executeUpdate();
        }
    }

    public List<AuditRecord> findByEntity(String entityType, long entityId) throws SQLException {
        List<AuditRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM audit WHERE entity_type=? AND entity_id=? ORDER BY changed_at DESC";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, entityType);
            ps.setLong(2, entityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuditRecord ar = new AuditRecord();
                    ar.setId(rs.getLong("id"));
                    ar.setEntityType(rs.getString("entity_type"));
                    ar.setEntityId(rs.getLong("entity_id"));
                    ar.setField(rs.getString("field"));
                    ar.setOldValue(rs.getString("old_value"));
                    ar.setNewValue(rs.getString("new_value"));
                    Timestamp ts = rs.getTimestamp("changed_at");
                    ar.setChangedAt(ts == null ? LocalDateTime.now() : ts.toLocalDateTime());
                    ar.setReason(rs.getString("reason"));
                    list.add(ar);
                }
            }
        }
        return list;
    }
}
