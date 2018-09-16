package lv.kauguri.iepirkumi.reactive;

import java.util.Deque;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class ChainedExecutor {

    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private Deque<Future> deque = new ConcurrentLinkedDeque<>();

    public void start(Runnable runnable) {
        deque.add(executorService.submit(new MyRunnable(runnable)));
    }

    public <T> void returns(T s, Consumer<T> consumer) {
        deque.add(executorService.submit(new MyRunnable(s, consumer)));
    }

    public void waitToFinish() {
        while(!deque.isEmpty()) {
            Future future = deque.remove();
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
    }
}
