package home.serg.chatik.dao;

public abstract class Entity <T>{
    private T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }
}
