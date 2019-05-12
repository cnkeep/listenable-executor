package github.com.cnkeep.listenable.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * 描述: 支持事件通知方式获取执行结果的线程池
 *
 * @author <a href="zhangleili924@gmail.com">LeiLi.Zhang</a>
 * @version 0.0.0
 * @date 2018/12/13
 */
public interface ListenableExecutorService extends ExecutorService{
    /**
     * 支持事件通知方式的执行结果返回
     * @param task
     * @param callBack 回调函数
     * @param <T>
     * @return
     */
    <T> void submit(Callable<T> task, FutureCallback<T> callBack);

    /**
     * 支持事件通知方式的执行结果返回
     * @param task
     * @param result
     * @param callBack 回调函数
     * @param <T>
     */
    <T> void submit(Runnable task, T result, FutureCallback<T> callBack);

    /**
     * 支持事件通知方式的执行结果返回
     * @param task
     * @param callBack 回调函数
     */
    void submit(Runnable task, FutureCallback<?> callBack);
}
