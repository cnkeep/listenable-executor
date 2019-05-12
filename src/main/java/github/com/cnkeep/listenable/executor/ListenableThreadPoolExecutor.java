package github.com.cnkeep.listenable.executor;

import java.util.concurrent.*;

/**
 * 描述: 支持执行结果事件回调的线程池, require min version of sdk1.8<br/>
 * <p>
 *     传统的需要采用Future.get方法去阻塞获取结果，这里采用重写FutureTask.done方法来实现异步回调，无需阻塞程序。
 * </p>
 * <pre>{@code
 *  ListenableExecutorService executor = new ListenableThreadPoolExecutor(
                                                1,
                                                1,
                                                0, TimeUnit.SECONDS,
                                                new LinkedBlockingDeque<>(),
                                                new ThreadFactoryBuilder().nameFormat("listable %d").build(),
                                                new ThreadPoolExecutor.AbortPolicy());
    Runnable successRunnable = () -> {
        System.out.println("run");
    };

    Runnable failRunnable = () -> {
        System.out.println("error");
        throw new RuntimeException("error");
    };

    FutureCallback<String> callBack = new FutureCallback<String>() {
        @Override
        public void success(Callable<String> task, String result) {
             System.out.println(task+" callback success with result:"+result);
        }
        @Override
        public void fail(Callable<String> task, Throwable cause) {
            System.out.println(task+" callback failed cause by:"+cause);
        }
    };

    executor.submit(successRunnable,callBack);
    executor.submit(failRunnable,callBack);

    executor.shutdown();
 *
 * }
 *
 * </pre>
 * @author <a href="zhangleili924@gmail.com">LeiLi.Zhang</a>
 * @version 0.0.0
 * @date 2018/12/13
 */
public class ListenableThreadPoolExecutor extends ThreadPoolExecutor implements ListenableExecutorService {
    public ListenableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public ListenableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public ListenableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public ListenableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new ListenableFutureTask<T>(runnable,value);
    }

    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, FutureCallback<T> callBack) {
        return new ListenableFutureTask<T>(runnable,null,callBack);
    }

    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value,FutureCallback<T> callBack) {
        return new ListenableFutureTask<T>(runnable,value,callBack);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new ListenableFutureTask<T>(callable);
    }

    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable, FutureCallback<T> callBack) {
        return new ListenableFutureTask<T>(callable,callBack);
    }

    @Override
    public <T> void submit(Callable<T> task, FutureCallback<T> callBack) {
        super.submit(newTaskFor(task,callBack));
    }

    @Override
    public <T> void submit(Runnable task, T result, FutureCallback<T> callBack) {
        super.submit(newTaskFor(task,result,callBack));
    }

    @Override
    public void submit(Runnable task, FutureCallback<?> callBack) {
        super.submit(newTaskFor(task,callBack));
    }

    public <T> void execute(Runnable command, FutureCallback<T> callBack) {
        super.execute(newTaskFor(command,callBack));
    }
}
