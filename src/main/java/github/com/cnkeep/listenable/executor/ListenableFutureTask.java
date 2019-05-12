package github.com.cnkeep.listenable.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 描述: 扩展FutureTask, 支持事件模型的异步操作, require min version of sdk1.8<br/>
 * <p>参见：com.google.common.util.concurrent.ListenableFutureTask</p>
 * @author <a href="zhangleili924@gmail.com">LeiLi.Zhang</a>
 * @version 0.0.0
 * @date 2018/12/13
 *
 */
public class ListenableFutureTask<V> extends FutureTask<V> {
    private Callable<V> target;
    private SuccessFutureCallBack<V> successCallBack;
    private FailFutureCallBack<V> failCallBack;

    public ListenableFutureTask(Callable<V> callable) {
        super(callable);
        target = callable;
    }

    public ListenableFutureTask(Callable<V> callable,FutureCallback<V> callBack) {
        super(callable);
        this.successCallBack = callBack;
        this.failCallBack = callBack;
    }

    public ListenableFutureTask(Runnable runnable, V result) {
        super(runnable, result);
        target = Executors.callable(runnable, result);
    }

    public ListenableFutureTask(Runnable runnable, V result,FutureCallback<V> callBack) {
        super(runnable, result);
        target = Executors.callable(runnable, result);
        this.successCallBack = callBack;
        this.failCallBack = callBack;
    }

    public void setCallBack(FutureCallback<V> callBack) {
        this.successCallBack = callBack;
        this.failCallBack = callBack;
    }

    public void setSuccessCallBack(SuccessFutureCallBack<V> successCallBack) {
        this.successCallBack = successCallBack;
    }

    public void setFailCallBack(FailFutureCallBack<V> failCallBack) {
        this.failCallBack = failCallBack;
    }

    /**
     * 扩展点
     */
    @Override
    protected void done() {
        Throwable cause = null;
        try {
            V result = get();
            if (null != this.successCallBack) {
                successCallBack.success(target, result);
            }
            return;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            cause = e.getCause();
            if (null == cause) {
                cause = e;
            }
        } catch (Throwable throwable) {
            cause = throwable;
        }
        if (null != this.failCallBack) {
            failCallBack.fail(target, cause);
        }
    }
}
