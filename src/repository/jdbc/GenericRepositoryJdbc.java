package repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public abstract class GenericRepositoryJdbc<T, ID> implements GenericRepository<T, ID> {

    protected Connection connection;

    public GenericRepositoryJdbc() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public abstract T save(T entity) throws SQLException;

    @Override
    public abstract Optional<T> findById(ID id) throws SQLException;

    @Override
    public abstract List<T> findAll() throws SQLException;

    @Override
    public abstract void update(T entity) throws SQLException;

    @Override
    public abstract void delete(ID id) throws SQLException;
}
