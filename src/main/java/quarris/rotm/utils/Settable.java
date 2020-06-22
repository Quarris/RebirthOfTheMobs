package quarris.rotm.utils;

public class Settable<T> {

    private T obj;

    Settable() { }

    public static <T> Settable<T> create() {
        return new Settable<>();
    }

    public T get() {
        return obj;
    }

    public void set(T obj) {
        this.obj = obj;
    }
}
