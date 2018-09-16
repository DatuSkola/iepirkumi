package lv.kauguri.iepirkumi.reactive;

import java.util.function.Consumer;

class MyRunnable<T> implements Runnable {
    T data = null;
    Consumer consumer;
    Runnable runnable;

    MyRunnable(T data, Consumer<T> consumer) {
        this.data = data;
        this.consumer = consumer;
    }

    MyRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        if(consumer != null) {
            consumer.accept(data);
        } else {
            runnable.run();
        }
    }
}