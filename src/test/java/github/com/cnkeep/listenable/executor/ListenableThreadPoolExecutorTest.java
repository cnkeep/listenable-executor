package github.com.cnkeep.listenable.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ListenableThreadPoolExecutorTest {
    public static void main(String[] args) {
        ListenableExecutorService executor = createListenableExecutorService();
        Runnable successRunnable = getSuccessRunnable();

        Runnable failRunnable = getFailRunnable();

        FutureCallback<String> callBack = getFutureCallback();

        executor.submit(successRunnable, callBack);
        executor.submit(failRunnable, callBack);

        executor.shutdown();
    }

    private static ListenableExecutorService createListenableExecutorService() {
        return new ListenableThreadPoolExecutor(
                    1,
                    1,
                    0, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(),
                    new ThreadFactoryBuilder().nameFormat("listable %d").build(),
                    new ThreadPoolExecutor.AbortPolicy());
    }

    private static Runnable getSuccessRunnable() {
        return () -> {
                System.out.println("run");
            };
    }

    private static Runnable getFailRunnable() {
        return () -> {
                System.out.println("error");
                throw new RuntimeException("error");
            };
    }

    private static FutureCallback<String> getFutureCallback() {
        return new FutureCallback<String>() {

                public void success(Callable<String> task, String result) {
                    System.out.println(task + " callback success with result:" + result);
                }

                public void fail(Callable<String> task, Throwable cause) {
                    System.out.println(task + " callback failed cause by:" + cause);
                }
            };
    }

}
