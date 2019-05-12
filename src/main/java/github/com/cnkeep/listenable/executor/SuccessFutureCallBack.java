package github.com.cnkeep.listenable.executor;

import java.util.concurrent.Callable;

/**
 * 描述: 执行成功后的回调
 *
 * @author <a href="zhangleili924@gmail.com">LeiLi.Zhang</a>
 * @version 0.0.0
 * @date 2018/12/13
 */
public interface SuccessFutureCallBack<V> {
    /**
     * 成功回调函数
     *
     * @param task
     * @param result 执行结果
     */
    void success(Callable<V> task, V result);
}
