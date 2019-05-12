package github.com.cnkeep.listenable.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述: 可以自定义名称的ThreadFactory, require min version of sdk1.8
 * <pre>{@code
 *  ThreadFactory threadFactory = new ThreadFactoryBuilder()
 *                                  .nameFormat("business %d")
 *                                  .builder();
 *  ThreadPoolService pool = new ThreadPoolExecutor(1,
 *                                  1,
 *                                  0, TimeUnit.SECONDS,
 *                                  new LinkedBlockingDeque<>(),
 *                                  new ThreadFactoryBuilder().nameFormat("listable %d").build(),
 *                                  new ThreadPoolExecutor.AbortPolicy());
 * }</pre>
 * @author <a href="zhangleili924@gmail.com">LeiLi.Zhang</a>
 * @version 0.0.0
 * @date 2018/10/5
 */
public final class ThreadFactoryBuilder {
    private static final AtomicInteger POOL_THREAD_NUMBER = new AtomicInteger(1);
    private final ThreadGroup group;
    private String namePrefix = "pool-%d-thread-";

    public ThreadFactoryBuilder() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
    }

    /**
     * 设置线程名称的模板
     *
     * @param format
     * @return
     */
    public ThreadFactoryBuilder nameFormat(String format) {
        namePrefix = format;
        return this;
    }

    /**
     * 构建ThreadFactory
     *
     * @return
     */
    public ThreadFactory build() {
        return (r) -> newThread(r);
    }

    private Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, String.format(namePrefix, POOL_THREAD_NUMBER.getAndIncrement()),
                0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
