package com.zmops.iot.async.wrapper;

import com.zmops.iot.async.callback.ICallback;
import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.util.collection.DirectedGraph;
import com.zmops.iot.async.util.collection.Graph;
import com.zmops.iot.async.wrapper.strategy.WrapperStrategy;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 快速构造{@link WorkerWrapper}，少废话！
 * <p>
 * 直接设置属性，不用麻烦Builder设置来设置去，
 * 请  注  意：构造方法不会检查参数合法性，请程序员自己保证参数合法。
 * <p>
 * 将关系存储于有向图中{@link DirectedGraph}以节省每个wrapper都要保存节点数据的开销。
 * </p>
 *
 * @author create by TcSnZh on 2021/5/13-上午11:54
 */
public class QuickBuildWorkerWrapper<T, V> extends WorkerWrapper<T, V> {
    private final DirectedGraph<WorkerWrapper<?, ?>, Object> graph;

    private volatile Set<WorkerWrapper<?, ?>> nextWrappersCache;
    private volatile Set<WorkerWrapper<?, ?>> dependWrappersCache;

    /**
     * 构造函数，传入所有属性
     *
     * @param id              {@link WorkerWrapper#id}
     * @param param           {@link WorkerWrapper#param}
     * @param worker          {@link WorkerWrapper#worker}
     * @param callback        {@link WorkerWrapper#callback}
     * @param allowInterrupt  {@link WorkerWrapper#allowInterrupt}
     * @param enableTimeout   {@link WorkerWrapper#enableTimeout}
     * @param timeoutLength   {@link WorkerWrapper#timeoutLength}
     * @param timeoutUnit     {@link WorkerWrapper#timeoutLength}
     * @param wrapperStrategy {@link WorkerWrapper#timeoutUnit}
     * @param wrapperGraph    将节点信息保存在图中，而不是如{@link StableWorkerWrapper}在每个wrapper中都保存节点信息。
     *                        <p>
     *                        {@link WorkerWrapper#getDependWrappers()}与{@link WorkerWrapper#getNextWrappers()}方法
     *                        将从本图中读取依赖顺序。除此之外，本类不会对本图进行任何修改操作。
     *                        因此，传入的此图应当保证读取时的线程安全。
     *                        </p>
     */
    public QuickBuildWorkerWrapper(String id,
                                   T param,
                                   IWorker<T, V> worker,
                                   ICallback<T, V> callback,
                                   boolean allowInterrupt,
                                   boolean enableTimeout,
                                   long timeoutLength,
                                   TimeUnit timeoutUnit,
                                   WrapperStrategy wrapperStrategy,
                                   DirectedGraph<WorkerWrapper<?, ?>, Object> wrapperGraph) {
        super(id, worker, callback, allowInterrupt, enableTimeout, timeoutLength, timeoutUnit, wrapperStrategy);
        graph = wrapperGraph;
        super.param = param;
        State.setState(state, State.BUILDING, State.INIT);
    }

    @Override
    public Set<WorkerWrapper<?, ?>> getNextWrappers() {
        if (nextWrappersCache == null) {
            synchronized (this) {
                if (nextWrappersCache == null) {
                    nextWrappersCache = graph.getRelationFrom(this).stream()
                            .map(Graph.Entry::getTo).collect(Collectors.toSet());
                }
            }
        }
        return nextWrappersCache;
    }

    @Override
    public Set<WorkerWrapper<?, ?>> getDependWrappers() {
        if (dependWrappersCache == null) {
            synchronized (this) {
                if (dependWrappersCache == null) {
                    dependWrappersCache = graph.getRelationTo(this).stream()
                            .map(Graph.Entry::getFrom).collect(Collectors.toSet());
                }
            }
        }
        return dependWrappersCache;
    }

    @Override
    void setNextWrappers(Set<WorkerWrapper<?, ?>> nextWrappers) {
        throw new UnsupportedOperationException();
    }

    @Override
    void setDependWrappers(Set<WorkerWrapper<?, ?>> dependWrappers) {
        throw new UnsupportedOperationException();
    }
}
