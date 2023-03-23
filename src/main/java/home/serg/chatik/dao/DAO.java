package home.serg.chatik.dao;

import java.util.Optional;

public interface DAO<T extends Entity<?>, E> {
    Optional<T> findById(E id);

    T save(T entity);

    T update(T entity);

    boolean deleteById(E id);
}
