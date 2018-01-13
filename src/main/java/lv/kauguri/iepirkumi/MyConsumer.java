package lv.kauguri.iepirkumi;

public interface MyConsumer<T, U> {
    void accept(T t, U u) throws Exception;
}
