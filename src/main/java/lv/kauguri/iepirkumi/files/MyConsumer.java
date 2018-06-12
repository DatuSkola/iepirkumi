package lv.kauguri.iepirkumi.files;

public interface MyConsumer<T, U> {
    void accept(T t, U u) throws Exception;
}
