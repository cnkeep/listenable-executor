package github.com.cnkeep.listenable.executor;

/**
 * 描述~
 *
 * @author <a href="zhangleili924@gmail.com">LeiLi.Zhang</a>
 * @version 0.0.0
 * @date 2018/12/13
 */
public interface FutureCallback<V> extends SuccessFutureCallBack<V>,FailFutureCallBack<V> {
}
