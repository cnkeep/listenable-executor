package github.com.cnkeep.listenable.executor;

import java.util.concurrent.Callable;

/**
 * 描述：执行失败后的回调
 *
 * @author <a href="zhangleili924@gmail.com">LeiLi.Zhang</a>
 * @version 0.0.0
 * @date 2018/12/13
 */
public interface FailFutureCallBack<V> {
    /**
     * 失败回调函数
     *
     * @param task
     * @param cause
     */
    void fail(Callable<V> task, Throwable cause);
}
