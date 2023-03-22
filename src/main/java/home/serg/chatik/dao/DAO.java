package home.serg.chatik.dao;

public interface DAO<T, E> {
    T findById(E id);

    T save(T entity);

    T update(T entity);

    boolean deleteById(E id);
}
